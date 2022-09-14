package com.nlmk.kb.server.service.pdm.senders;

import com.nlmk.kb.server.entity.pdm.PdmMessage;
import com.nlmk.kb.server.entity.pdm.PdmOp;
import com.nlmk.kb.server.service.pdm.DictionaryConfigService;
import com.nlmk.kb.server.service.sender.NsiSender;
import com.nlmk.kb.server.service.pdm.PdmDictionaryCreator;
import com.nlmk.kb.server.service.pdm.PdmDtoConverter;
import com.nlmk.kb.server.service.pdm.PdmMessageCreator;
import nlmk.l3.pdm.SpKatSteelMarkGost4041;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class KatSteel4041MessageSender extends BasePdmCreator implements PdmMessageSender, PdmMessageCreator {

    public KatSteel4041MessageSender(@Value("${kafka.pdm.topic.kat-steel-4041}") String type,
                                     PdmDtoConverter pdmDtoConverter,
                                     NsiSender commonSender,
                                     PdmDictionaryCreator pdmDictionaryCreator,
                                     DictionaryConfigService dictionaryConfigService) {
        super(type, pdmDtoConverter, commonSender, pdmDictionaryCreator, dictionaryConfigService);
    }

    @Override
    public ResponseEntity<Long> send(PdmMessage message) {
        Assert.notNull(message, () -> {
            throw new IllegalArgumentException("message for sending is NULL");
        });

        return super.getNsiSender().exchange(
                super.getPdmDtoConverter().toKatSteel4041Dto(message.getDictionary()),
                super.getDictionaryConfigService().getDictionaryUrlByTopic(message.getTopic()),
                message.getOp()
        );
    }

    @Override
    public PdmMessage createPdmMessage(ConsumerRecord<Object, Object> consumerRecord) {
        SpKatSteelMarkGost4041 pdmObject = (SpKatSteelMarkGost4041) consumerRecord.value();

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
