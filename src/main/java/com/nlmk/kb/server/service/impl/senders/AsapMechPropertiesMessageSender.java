package com.nlmk.kb.server.service.impl.senders;

import com.nlmk.kb.server.config.KbConstants;
import com.nlmk.kb.server.entity.pdm.PdmMessage;
import com.nlmk.kb.server.service.DictionaryConfigService;
import com.nlmk.kb.server.service.MessageSender;
import com.nlmk.kb.server.service.NsiCommonSender;
import com.nlmk.kb.server.service.PdmDictionaryCreator;
import com.nlmk.kb.server.service.PdmDtoConverter;
import com.nlmk.kb.server.service.PdmMessageCreator;
import com.nlmk.kb.server.util.RestTemplateUtils;
import lombok.extern.slf4j.Slf4j;
import nlmk.l3.pdm.SpAsapMechProperties;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Slf4j
@Service
public class AsapMechPropertiesMessageSender extends BaseSender implements MessageSender, PdmMessageCreator {

    public AsapMechPropertiesMessageSender(@Value("${kafka.pdm.topic.asap-mech-properties}") String type,
                                           NsiCommonSender commonSender,
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

        final var sendingDto = super.getPdmDtoConverter().toPhysMechPropertiesDto(message.getDictionary());

        final var headers = RestTemplateUtils.prepareHeaders(MDC.get(KbConstants.KAFKA_ID));
        final var request = new HttpEntity<>(sendingDto, headers);
        final var nsiUrl = getDictionaryConfigService().getDictionaryUrlByTopic(message.getTopic());

        return super.getCommonSender().exchange(request, nsiUrl, message.getOp());
    }

    @Override
    public PdmMessage createPdmMessage(ConsumerRecord record) {
        SpAsapMechProperties pdmObject = (SpAsapMechProperties) record.value();

        final var dictionary = super.getPdmDictionaryCreator().createPdmDictionary(
                pdmObject.getTs(), pdmObject.getOp(), pdmObject.getPk(), pdmObject.getData()
        );

        final var message = PdmMessage.builder()
                .topic(record.topic())
                .key((String) record.key())
                .offset(record.offset())
                .partition(record.partition())
                .dictionary(dictionary)
                .op(dictionary.getOp())
                .ts(dictionary.getTs())
                .build();

        return message;
    }
}
