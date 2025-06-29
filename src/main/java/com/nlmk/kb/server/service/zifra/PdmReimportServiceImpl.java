package com.nlmk.kb.server.service.zifra;

import com.nlmk.kb.server.api.ReimportDto;
import com.nlmk.kb.server.api.ReimportRequestDto;
import com.nlmk.kb.server.api.ReimportState;
import com.nlmk.kb.server.api.ReimportType;
import com.nlmk.kb.server.entity.Reimport;
import com.nlmk.kb.server.entity.pdm.PdmDictionary;
import com.nlmk.kb.server.entity.pdm.PdmMessage;
import com.nlmk.kb.server.exception.RemoteServiceSenderException;
import com.nlmk.kb.server.mapper.ReimportMapper;
import com.nlmk.kb.server.repository.ReimportRepository;
import com.nlmk.kb.server.service.pdm.PdmMessageService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service("PdmReimportServiceImpl")
@RequiredArgsConstructor
public class PdmReimportServiceImpl implements ReimportService {

    private final ReimportRepository reimportRepository;
    private final PdmMessageService messageService;
    private final ReimportStateService reimportStateService;
    private final ReimportMapper reimportMapper;
    private final EntityManager entityManager;

    private static final Integer MAX_REIMPORT_SECONDS = 60;
    private static final String REIMPORT_STOPPED = "реимпорт остановлен";
    private static final String REIMPORT_FINISHED = "реимпорт завершен";

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

        ReimportResult result = processMessages(requestDto, reimport);

        completeReimport(reimport, result);

        return buildResultMessage(result);

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
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

    private ReimportResult processMessages(
            ReimportRequestDto requestDto,
            Reimport reimport) {
        ReimportResult result = new ReimportResult();
        Long lastProcessedId = null;

        try {
            List<PdmMessage> messages = findAllMessages(lastProcessedId, requestDto);
            for (PdmMessage message : messages) {
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
        ReimportState refreshed = reimportStateService.getLatestState(reimport.getName());
        reimport.setLastImportedId(lastProcessedId);
        reimport.setLastImportDate(Instant.now());
        reimport.setState(refreshed);
        reimportRepository.save(reimport);
        log.debug("Обновлен прогресс реимпорта: последний обработанный ID={}", lastProcessedId);
    }

    private List<PdmMessage> findAllMessages(Long lastId, ReimportRequestDto filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<PdmMessage> query = cb.createQuery(PdmMessage.class);
        Root<PdmMessage> root = query.from(PdmMessage.class);

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
            predicates.add(cb.greaterThanOrEqualTo(root.get("ts"),
                    Date.from(filter.getDstart())));
        }
        if (filter.getDend() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("ts"), Date.from(filter.getDend())));
        }

        query.where(cb.and(predicates.toArray(new Predicate[0])))
                .orderBy(cb.asc(root.get("id")));

        return entityManager.createQuery(query)
                .getResultList();
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

    private boolean validateMessage(PdmMessage message) {
        PdmDictionary dictionary = message.getDictionary();
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
    public void processSingleMessage(PdmMessage message, ReimportResult result) throws
                                                                                RemoteServiceSenderException {
        try {
            messageService.sendToNsi(message);
            result.incrementProcessed();
        } catch (RemoteServiceSenderException ex) {
            log.error("reimport, ошибка отправки в НСИ, сообщение [{}]", ex.getMessage());
            result.incrementErrors();
        } catch (Exception e) {
            log.error("reimport, ошибка обработки сообщения ID={}", message.getId(), e);
            result.incrementErrors();
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