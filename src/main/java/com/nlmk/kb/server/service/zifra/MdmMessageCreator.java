package com.nlmk.kb.server.service.zifra;

import com.nlmk.kb.server.entity.mdm.MdmMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface MdmMessageCreator {

    String getType();
    MdmMessage createMdmMessage(ConsumerRecord<Object, Object> consumerRecord);

}
