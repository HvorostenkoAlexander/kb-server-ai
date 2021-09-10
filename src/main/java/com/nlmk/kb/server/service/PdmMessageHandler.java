package com.nlmk.kb.server.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface PdmMessageHandler {

    boolean handleConsumerRecord(ConsumerRecord record);
}
