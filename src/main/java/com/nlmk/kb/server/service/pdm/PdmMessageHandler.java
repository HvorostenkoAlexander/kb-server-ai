package com.nlmk.kb.server.service.pdm;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface PdmMessageHandler {

    boolean handleConsumerRecord(ConsumerRecord<Object, Object> record);

}
