package com.nlmk.kb.server.service.pdm;

import com.nlmk.kb.server.entity.pdm.PdmMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface PdmMessageConverter {

    PdmMessage fromConsumerRecord(ConsumerRecord<Object, Object> consumerRecord);

}
