package com.nlmk.kb.server.service.pdm.senders;

import com.nlmk.kb.server.entity.Operation;
import com.nlmk.kb.server.entity.pdm.PdmDictionary;
import com.nlmk.kb.server.entity.pdm.PdmMessage;
import com.nlmk.kb.server.service.pdm.DictionaryConfigService;
import com.nlmk.kb.server.service.pdm.PdmMessageCreator;
import com.nlmk.kb.server.service.sender.NsiSender;
import com.nlmk.kb.server.service.pdm.PdmDictionaryCreator;
import com.nlmk.kb.server.service.pdm.PdmDtoConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.util.Assert;

@Getter
@AllArgsConstructor
abstract class BasePdmCreator implements PdmMessageCreator, PdmMessageSender {

    private final String type;
    private final PdmDtoConverter pdmDtoConverter;
    private final NsiSender nsiSender;
    private final PdmDictionaryCreator pdmDictionaryCreator;
    private final DictionaryConfigService dictionaryConfigService;

    @Override
    public Long send(PdmMessage message) {
        Assert.notNull(message, () -> {
            throw new IllegalArgumentException("Нет сообщения для отправки (NULL)");
        });

        return nsiSender.sendBodyReturnLong(
                getBody(message),
                dictionaryConfigService.getDictionaryUrlByTopic(message.getTopic()),
                message.getOp()
        );
    }

    abstract Object getBody(PdmMessage message);

    @Override
    public PdmMessage createPdmMessage(ConsumerRecord<Object, Object> consumerRecord) {

        final var dictionary = getDictionary(consumerRecord);

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

    abstract PdmDictionary getDictionary(ConsumerRecord<Object, Object> consumerRecord);

}
