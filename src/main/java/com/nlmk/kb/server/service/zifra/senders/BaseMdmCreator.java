package com.nlmk.kb.server.service.zifra.senders;

import com.nlmk.kb.server.entity.mdm.MdmDictionary;
import com.nlmk.kb.server.entity.mdm.MdmMessage;
import com.nlmk.kb.server.service.zifra.MdmDictionaryCreator;
import com.nlmk.kb.server.service.zifra.MdmMessageCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import nlmk.l3.nsi.zifra.Reason;
import org.apache.kafka.clients.consumer.ConsumerRecord;

@Getter
@AllArgsConstructor
abstract class BaseMdmCreator implements MdmMessageCreator {

    private final MdmDictionaryCreator mdmDictionaryCreator;
    private final String type;

    public MdmMessage createMdmMessage(ConsumerRecord<Object, Object> consumerRecord) {

        final var dictionary = getDictionary(consumerRecord);

        return MdmMessage.builder()
                .topic(consumerRecord.topic())
                .offset(consumerRecord.offset())
                .partition(consumerRecord.partition())
                .dictionary(dictionary)
                .ts(dictionary.getTs())
                .build();
    }

    public MdmDictionary getDictionary(ConsumerRecord<Object, Object> consumerRecord) {
        final var mdmObject = (Reason) consumerRecord.value();
        return mdmDictionaryCreator.createMdmDictionary(
                mdmObject.getTs(), mdmObject.getOp(), mdmObject.getPk(), mdmObject.getData()
        );
    }

}
