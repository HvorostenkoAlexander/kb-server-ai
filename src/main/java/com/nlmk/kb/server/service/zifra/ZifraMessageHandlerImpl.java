package com.nlmk.kb.server.service.zifra;

import com.nlmk.kb.server.entity.Operation;
import com.nlmk.kb.server.entity.mdm.MdmMessage;
import com.nlmk.kb.server.exception.RemoteServiceSenderException;
import com.nlmk.kb.server.service.sender.NsiSender;
import com.nlmk.kb.server.util.AdapterUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nlmk.l3.nsi.zifra.EnumOp;
import nlmk.l3.nsi.zifra.Reason;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ZifraMessageHandlerImpl implements ZifraMessageHandler {

    private Map<Catalogue, CatalogueParser<?>> parserMap;
    private final NsiSender nsiSender;
    private final MdmMessageConverter messageConverter;
    private final MdmMessageService messageService;

    @Autowired
    public void setCatalogueParsers(List<CatalogueParser<?>> catalogueParsers) {
        this.parserMap = catalogueParsers.stream().collect(Collectors.toMap(
                CatalogueParser::getCatalogue, Function.identity()
        ));
    }

    @Override
    public boolean handleConsumerRecord(ConsumerRecord<Object, Object> consumerRecord) {
        if (Objects.isNull(consumerRecord.value())) {
            log.warn("handleConsumerRecord, пропуск, пустое тело сообщения: topic [{}], partition [{}], offset [{}], key [{}]",
                    consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset(), consumerRecord.key());
            return true;
        }

        MdmMessage message = messageConverter.fromConsumerRecord(consumerRecord);
        Optional<MdmMessage> mdmMessage = messageService.save(message);

        if (mdmMessage.isPresent()) {
            // ожидаемый формат сообщения, согласно AVRO схеме
            final var value = (Reason) consumerRecord.value();

            if (Objects.isNull(value.getPk())
                    || Objects.isNull(value.getData())
                    || Objects.isNull(value.getOp())) {
                log.warn("handleConsumerRecord, пропуск, нет нужного набора данных (pk, data, op): topic [{}], partition [{}], offset [{}], key [{}]",
                        consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset(), consumerRecord.key());
                return true;
            }

            // какой справочник?
            final var catalogCode = AdapterUtils.sequenceToString(value.getData().getCatalogCode());
            final var catalogue = Catalogue.fromCode(catalogCode);
            if (Objects.isNull(catalogue)) {
                log.warn("handleConsumerRecord, неизвестный Каталог, код [{}]", catalogCode);
                return true;
            }

            final var operation = transformOperation(value.getOp());
            final var parser = parserMap.get(catalogue);
            final var dto = parser.parse(value.getPk(), value.getData(), mdmMessage.get().getId());

            try {
                final var response = nsiSender.sendBodyReturnString(dto, catalogue.getPath(), operation, mdmMessage.get().getId());
                log.info("handleConsumerRecord, объект отправлен, ответ НСИ [{}], Каталог [{}], путь [{}], операция [{}]",
                        response, catalogue, catalogue.getPath(), operation);
                if (response != null) {
                    String[] resp = response.split(":");
                    mdmMessage.get().setNote(resp[0]);
                    messageService.update(mdmMessage.get());
                }
                return true;
            } catch (RemoteServiceSenderException e) {
                log.error("handleConsumerRecord, ошибка отправки в НСИ, DTO [{}], Каталог [{}], сообщение [{}]",
                        dto, catalogue, e.getMessage());
                mdmMessage.get().setNote("ERROR: " + e.getMessage());
                messageService.update(mdmMessage.get());
                return false;
            }
        }
        return false;
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

}
