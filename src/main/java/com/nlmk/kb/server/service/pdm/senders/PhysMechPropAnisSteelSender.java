package com.nlmk.kb.server.service.pdm.senders;

import com.nlmk.kb.server.config.KbConstants;
import com.nlmk.kb.server.entity.pdm.PdmMessage;
import com.nlmk.kb.server.entity.pdm.PdmOp;
import com.nlmk.kb.server.service.pdm.DictionaryConfigService;
import com.nlmk.kb.server.service.pdm.PdmDictionaryCreator;
import com.nlmk.kb.server.service.pdm.PdmDtoConverter;
import com.nlmk.kb.server.service.pdm.PdmMessageCreator;
import com.nlmk.kb.server.service.sender.NsiSender;
import com.nlmk.kb.server.util.RestTemplateUtils;
import nlmk.l3.pdm.SpPhysMechPropAnisSteelStand;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

public class PhysMechPropAnisSteelSender extends BaseCreator implements MessageSender, PdmMessageCreator {

    public PhysMechPropAnisSteelSender(@Value("${kafka.pdm.topic.phys-mech-prop-anis-steel}") String type,
                                       PdmDtoConverter pdmDtoConverter,
                                       NsiSender nsiSender,
                                       PdmDictionaryCreator pdmDictionaryCreator,
                                       DictionaryConfigService dictionaryConfigService) {
        super(type, pdmDtoConverter, nsiSender, pdmDictionaryCreator, dictionaryConfigService);
    }

    @Override
    public ResponseEntity<Long> send(PdmMessage message) {
        Assert.notNull(message, () -> {
            throw new IllegalArgumentException("message for sending is NULL");
        });

        final var sendingDto = super.getPdmDtoConverter().toPhysMechPropAnisSteelStandDto(message.getDictionary());

        final var headers = RestTemplateUtils.prepareHeaders(MDC.get(KbConstants.KAFKA_ID));
        final var request = new HttpEntity<>(sendingDto, headers);
        final var nsiUrl = super.getDictionaryConfigService().getDictionaryUrlByTopic(message.getTopic());

        return super.getNsiSender().exchange(request, nsiUrl, message.getOp());
    }

    @Override
    public PdmMessage createPdmMessage(ConsumerRecord<Object, Object> consumerRecord) {
        final var pdmObject = (SpPhysMechPropAnisSteelStand) consumerRecord.value();

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
