package com.nlmk.kb.server.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface SapMessageHandler {

    boolean handleConsumerRecord(ConsumerRecord<Object, Object> record);
}
