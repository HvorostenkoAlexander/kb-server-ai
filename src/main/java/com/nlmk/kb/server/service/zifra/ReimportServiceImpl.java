package com.nlmk.kb.server.service.zifra;


import com.nlmk.kb.server.api.ReimportDto;
import com.nlmk.kb.server.api.ReimportRequestDto;
import com.nlmk.kb.server.api.ReimportState;
import com.nlmk.kb.server.api.ReimportType;
import com.nlmk.kb.server.entity.Operation;
import com.nlmk.kb.server.entity.Reimport;
import com.nlmk.kb.server.entity.mdm.MdmDictionary;
import com.nlmk.kb.server.entity.mdm.MdmMessage;
import com.nlmk.kb.server.exception.RemoteServiceSenderException;
import com.nlmk.kb.server.mapper.ReimportMapper;
import com.nlmk.kb.server.repository.ReimportRepository;
import com.nlmk.kb.server.service.sender.NsiSender;
import com.nlmk.kb.server.util.AdapterUtils;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import nlmk.l3.nsi.zifra.Data;
import nlmk.l3.nsi.zifra.EnumOp;
import nlmk.l3.nsi.zifra.pk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReimportServiceImpl implements ReimportService {

    private final ReimportRepository reimportRepository;
    private Map<Catalogue, CatalogueParser<?>> parserMap;
    private final NsiSender nsiSender;
    private final MdmMessageService messageService;
    private final MdmDictionaryCreatorImpl mdmDictionaryCreator;
    private final ReimportMapper reimportMapper;
    private final EntityManager entityManager;

    private static final Integer MAX_REIMPORT_SECONDS = 60;
    private static final String REIMPORT_STOPPED = "реимпорт остановлен";
    private static final String REIMPORT_FINISHED = "реимпорт завершен";

    @Autowired
    public void setCatalogueParsers(List<CatalogueParser<?>> catalogueParsers) {
        this.parserMap = catalogueParsers.stream().collect(Collectors.toMap(
                CatalogueParser::getCatalogue, Function.identity()
        ));
    }


    @Override
    public String startReimport(ReimportType reimportType, ReimportRequestDto requestDto) {
        log.info("Запуск реимпорта типа {} с параметрами: {}", reimportType, requestDto);

        // Получаем или создаем запись реимпорта
        Reimport reimport = reimportRepository.findById(reimportType)
                .orElseGet(() -> Reimport.builder().name(reimportType).build());

        // Проверка на уже выполняющийся реимпорт
        if (isReimportRunning(reimport)) {
            String msg = "Реимпорт уже выполняется";
            log.warn(msg);
            return msg;
        }

        // Инициализация реимпорта
        initReimport(reimport);

        ReimportResult result = processMessages(reimportType, requestDto, reimport);

        completeReimport(reimport, result);

        return buildResultMessage(result);

    }

    @Override
    @Transactional
    public ReimportDto stopReimport(ReimportType reimportType) {
        var state = reimportRepository.findById(reimportType)
                .orElse(Reimport.builder().name(reimportType).build());

        state.setState(ReimportState.STOP);
        state.setErrorMessage(REIMPORT_STOPPED);

        return reimportMapper.toDto(reimportRepository.save(state));
    }

    @Override
    @Transactional(readOnly = true)
    public ReimportDto getReimportState(ReimportType reimportType) {
        return reimportRepository.findById(reimportType)
                .map(reimportMapper::toDto)
                .orElseGet(() -> ReimportDto.builder()
                        .name(reimportType)
                        .state(ReimportState.STOP)
                        .build());
    }

    private ReimportResult processMessages(ReimportType reimportType,
                                           ReimportRequestDto requestDto,
                                           Reimport reimport) {
        ReimportResult result = new ReimportResult();
        Long lastProcessedId = null;

        try {
            List<MdmMessage> messages = findAllMessages(lastProcessedId, requestDto);
            for (MdmMessage message : messages) {
                if (reimport.getState() == ReimportState.STOP) {
                    result.setFinalStatus(REIMPORT_STOPPED);
                    break;
                }

                if (!validateMessage(message)) {
                    log.warn("Пропуск сообщения с некорректными данными: {}", message.getId());
                } else {
                    processSingleMessage(message, result);
                    log.info("Успешно обработано сообщение ID={}", message.getId());
                    lastProcessedId = message.getId();
                    updateReimportProgress(reimport, lastProcessedId);
                    reimport = reimportRepository.findById(reimportType).orElse(reimport);
                }
            }
        } catch (Exception e) {
            log.error("Ошибка при выполнении реимпорта", e);
            result.setFinalStatus("Ошибка при выполнении реимпорта: " + e.getMessage());
        } finally {
            log.info("Реимпорт завершен. Обработано сообщений: {}, с ошибками: {}",
                    result.getProcessedCount(), result.getErrorCount());
        }

        result.setLastProcessedId(lastProcessedId);
        return result;
    }

    private void updateReimportProgress(Reimport reimport, Long lastProcessedId) {
        reimport.setLastImportedId(lastProcessedId);
        reimport.setLastImportDate(Instant.now());
        reimportRepository.save(reimport);
        log.debug("Обновлен прогресс реимпорта: последний обработанный ID={}", lastProcessedId);
    }

    private List<MdmMessage> findAllMessages(Long lastId, ReimportRequestDto filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<MdmMessage> query = cb.createQuery(MdmMessage.class);
        Root<MdmMessage> root = query.from(MdmMessage.class);

        List<Predicate> predicates = new ArrayList<>();

        // Добавляем фильтр по ID только если он указан в запросе
        if (filter.getId() != null) {
            predicates.add(cb.equal(root.get("id"), filter.getId()));
        } else if (lastId != null) {
            predicates.add(cb.greaterThan(root.get("id"), lastId));
        }

        if (filter.getTopic() != null) {
            predicates.add(cb.equal(root.get("topic"), filter.getTopic()));
        }
        if (filter.getNote() != null) {
            predicates.add(cb.like(root.get("note"), "%" + filter.getNote() + "%"));
        }
        if (filter.getDstart() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("tsTimestamp"), filter.getDstart()));
        }
        if (filter.getDend() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("tsTimestamp"), filter.getDend()));
        }

        query.where(cb.and(predicates.toArray(new Predicate[0])))
                .orderBy(cb.asc(root.get("id")));

        return entityManager.createQuery(query)
                .getResultList();
    }

    private Operation transformOperation(EnumOp enumOp) {
        switch (enumOp) {
            case I:
                return Operation.I;
            case U:
                return Operation.U;
            case D:
                return Operation.D;
            default:
                return null;
        }
    }

    private boolean isReimportRunning(Reimport reimport) {
        return reimport.getState() == ReimportState.RUN
                && reimport.getLastImportDate() != null
                && reimport.getLastImportDate().isAfter(Instant.now().minusSeconds(MAX_REIMPORT_SECONDS));
    }

    private void initReimport(Reimport reimport) {
        reimport.setState(ReimportState.RUN);
        reimport.setLastImportDate(Instant.now());
        reimport.setErrorMessage(null);
        reimportRepository.save(reimport);
    }

    private boolean validateMessage(MdmMessage message) {
        MdmDictionary dictionary = message.getDictionary();
        if (dictionary == null) {
            log.warn("Нет словаря для сообщения {}", message.getId());
            return false;
        }

        if (dictionary.getPk() == null || dictionary.getData() == null || dictionary.getOp() == null) {
            log.warn("Неполные данные в словаре для сообщения {}", message.getId());
            return false;
        }
        return true;
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processSingleMessage(MdmMessage message, ReimportResult result) throws RemoteServiceSenderException {
        MdmDictionary dictionary = message.getDictionary();
        String catalogCode = AdapterUtils.sequenceToString(dictionary.getData().getCatalogCode());
        Catalogue catalogue = Catalogue.fromCode(catalogCode);
        Object dto = new Object();
        if (catalogue == null) {
            throw new RemoteServiceSenderException("Неизвестный каталог: " + catalogCode);
        }
        try {
            Operation operation = transformOperation(EnumOp.valueOf(dictionary.getOp()));
            CatalogueParser<?> parser = parserMap.get(catalogue);

            pk pk = nlmk.l3.nsi.zifra.pk.newBuilder()
                    .setLineId(dictionary.getPk().getLineId())
                    .setSystemCode(dictionary.getPk().getSystemCode())
                    .build();

            Data data = mdmDictionaryCreator.fromMdmData(dictionary.getData());
            dto = parser.parse(pk, data, message.getId());

            String response = nsiSender.sendBodyReturnString(dto, catalogue.getPath(), operation, message.getId());
            log.info("Сообщение {} отправлено в НСИ. Ответ: {}", message.getId(), response);
            if (operation != null) {
                if (operation.getHttpMethod().equals(HttpMethod.POST) || operation.getHttpMethod()
                        .equals(HttpMethod.PUT)) {
                    String[] resp = response.split(":");
                    message.setNote(resp[0]);
                } else if (operation.getHttpMethod().equals(HttpMethod.DELETE)) {
                    message.setNote("OK");
                }
                messageService.update(message);
            }
            result.incrementProcessed();
        } catch (RemoteServiceSenderException ex) {
            log.error("reimport, ошибка отправки в НСИ, DTO [{}], Каталог [{}], сообщение [{}]",
                    dto, catalogue, ex.getMessage());
            message.setNote("ERROR: " + ex.getMessage());
            messageService.update(message);
            result.incrementErrors();
        } catch (Exception e) {
            log.error("reimport, ошибка обработки сообщения ID={}", message.getId(), e);
            result.incrementErrors();
            message.setNote("ERROR: " + e.getMessage());
            messageService.update(message);
        }
    }

    @Getter
    @Setter
    private static class ReimportResult {

        private int processedCount = 0;
        private int errorCount = 0;
        private Long lastProcessedId = null;
        private String finalStatus = REIMPORT_FINISHED;

        public void incrementProcessed() {
            this.processedCount++;
        }

        public void incrementErrors() {
            this.errorCount++;
            this.finalStatus = "Реимпорт завершен с ошибками";
        }

        public boolean hasErrors() {
            return errorCount > 0;
        }
    }

    private String buildResultMessage(ReimportResult result) {
        return String.format("%s. Обработано: %d, с ошибками: %d",
                result.getFinalStatus(), result.getProcessedCount(), result.getErrorCount());
    }

    private void completeReimport(Reimport reimport, ReimportResult result) {
        if (reimport.getState() != ReimportState.STOP) {
            reimport.setState(ReimportState.STOP);
            reimport.setLastImportDate(Instant.now());
            reimport.setLastImportedId(result.getLastProcessedId());
            reimport.setErrorMessage(result.hasErrors() ? result.getFinalStatus() : null);
            reimportRepository.save(reimport);
        }
    }
}
