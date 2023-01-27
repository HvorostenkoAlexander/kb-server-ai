package com.nlmk.kb.server.service.pdm.senders;

import com.nlmk.kb.server.entity.Operation;
import com.nlmk.kb.server.entity.pdm.PdmMessage;
import com.nlmk.kb.server.service.pdm.DictionaryConfigService;
import com.nlmk.kb.server.service.pdm.PdmDictionaryCreator;
import com.nlmk.kb.server.service.pdm.PdmDtoConverter;
import com.nlmk.kb.server.service.pdm.PdmMessageCreator;
import com.nlmk.kb.server.service.sender.NsiSender;
import nlmk.l3.pdm.SpSchemeStrippingSlab;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class SchemeStrippingSlabSender extends BasePdmCreator implements PdmMessageSender, PdmMessageCreator {

    public SchemeStrippingSlabSender(@Value("${kafka.pdm.topic.scheme-stripping-slab}") String type,
                                     NsiSender commonSender,
                                     PdmDtoConverter pdmDtoConverter,
                                     PdmDictionaryCreator pdmDictionaryCreator,
                                     DictionaryConfigService dictionaryConfigService) {
        super(type, pdmDtoConverter, commonSender, pdmDictionaryCreator, dictionaryConfigService);
    }

    @Override
    public Long send(PdmMessage message) {
        Assert.notNull(message, () -> {
            throw new IllegalArgumentException("message for sending is NULL");
        });

        return super.getNsiSender().sendBodyReturnLong(
                super.getPdmDtoConverter().toSchemeStrippingSlabDto(message.getDictionary()),
                super.getDictionaryConfigService().getDictionaryUrlByTopic(message.getTopic()),
                message.getOp()
        );
    }

    @Override
    public PdmMessage createPdmMessage(ConsumerRecord<Object, Object> consumerRecord) {
        SpSchemeStrippingSlab pdmObject = (SpSchemeStrippingSlab) consumerRecord.value();

        final var dictionary = super.getPdmDictionaryCreator().createPdmDictionary(
                pdmObject.getTs(), pdmObject.getOp(), pdmObject.getPk(), pdmObject.getData()
        );

        return PdmMessage.builder()
                .topic(consumerRecord.topic())
                .key((String) consumerRecord.key())
                .offset(consumerRecord.offset())
                .partition(consumerRecord.partition())
                .dictionary(dictionary)
                .op(Operation.fromValue(dictionary.getOp()))
                .ts(dictionary.getTs())
                .build();
    }

}
