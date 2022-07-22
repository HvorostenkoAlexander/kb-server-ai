package com.nlmk.kb.server.service.pdm.senders;

import com.nlmk.kb.server.config.KbConstants;
import com.nlmk.kb.server.entity.pdm.PdmMessage;
import com.nlmk.kb.server.entity.pdm.PdmOp;
import com.nlmk.kb.server.service.pdm.DictionaryConfigService;
import com.nlmk.kb.server.service.sender.NsiSender;
import com.nlmk.kb.server.service.pdm.PdmDictionaryCreator;
import com.nlmk.kb.server.service.pdm.PdmDtoConverter;
import com.nlmk.kb.server.service.pdm.PdmMessageCreator;
import com.nlmk.kb.server.util.RestTemplateUtils;
import lombok.extern.slf4j.Slf4j;
import nlmk.l3.pdm.SpAsapChemicalProperties;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Slf4j
@Service
public class AsapChemicalPropMessageSender extends BaseCreator implements MessageSender, PdmMessageCreator {

    public AsapChemicalPropMessageSender(@Value("${kafka.pdm.topic.asap-chemical-properties}") String type,
                                         NsiSender commonSender,
                                         PdmDtoConverter pdmDtoConverter,
                                         PdmDictionaryCreator pdmDictionaryCreator,
                                         DictionaryConfigService dictionaryConfigService) {
        super(type, pdmDtoConverter, commonSender, pdmDictionaryCreator, dictionaryConfigService);
    }

    @Override
    public ResponseEntity<Long> send(PdmMessage message) {
        Assert.notNull(message, () -> {
            throw new IllegalArgumentException("message for sending is NULL");
        });

        final var sendingDto = super.getPdmDtoConverter().toChemicalStdLimitDto(message.getDictionary());

        final var headers = RestTemplateUtils.prepareHeaders(MDC.get(KbConstants.KAFKA_ID));
        final var request = new HttpEntity<>(sendingDto, headers);
        final var nsiUrl = getDictionaryConfigService().getDictionaryUrlByTopic(message.getTopic());

        return super.getCommonSender().exchange(request, nsiUrl, message.getOp());
    }

    @Override
    public PdmMessage createPdmMessage(ConsumerRecord<Object, Object> consumerRecord) {
        final var pdmObject = (SpAsapChemicalProperties) consumerRecord.value();

        final var dictionary = super.getPdmDictionaryCreator().createPdmDictionary(
                pdmObject.getTs(), pdmObject.getOp(), pdmObject.getPk(), pdmObject.getData()
        );

        return PdmMessage.builder()
                .topic(consumerRecord.topic())
                .key((String) consumerRecord.key())
                .offset(consumerRecord.offset())
                .partition(consumerRecord.partition())
                .dictionary(dictionary)
                .op(PdmOp.fromValue(dictionary.getOp()))
                .ts(dictionary.getTs())
                .build();
    }

}
