package com.nlmk.kb.server.service.pdm.senders;

import com.nlmk.kb.server.entity.pdm.PdmMessage;
import com.nlmk.kb.server.entity.Operation;
import com.nlmk.kb.server.service.pdm.DictionaryConfigService;
import com.nlmk.kb.server.service.sender.NsiSender;
import com.nlmk.kb.server.service.pdm.PdmDictionaryCreator;
import com.nlmk.kb.server.service.pdm.PdmDtoConverter;
import com.nlmk.kb.server.service.pdm.PdmMessageCreator;
import nlmk.l3.pdm.SpMatchRabplanNum;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class MatchRpNumMessageSender extends BasePdmCreator implements PdmMessageSender, PdmMessageCreator {

    public MatchRpNumMessageSender(@Value("${kafka.pdm.topic.match-rabplan-num}") String type,
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

        return super.getNsiSender().sendBodyReturnLong(
                super.getPdmDtoConverter().toMatchRpDto(message.getDictionary()),
                super.getDictionaryConfigService().getDictionaryUrlByTopic(message.getTopic()),
                message.getOp()
        );
    }

    @Override
    public PdmMessage createPdmMessage(ConsumerRecord<Object, Object> consumerRecord) {
        SpMatchRabplanNum pdmObject = (SpMatchRabplanNum) consumerRecord.value();

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
