package com.nlmk.kb.server.service;

import com.nlmk.s3.proxy.s3notification;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface SapMessageHandler {

    boolean handleConsumerRecord(ConsumerRecord<String, s3notification> record);
}
