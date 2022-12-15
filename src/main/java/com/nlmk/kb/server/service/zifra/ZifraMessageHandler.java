package com.nlmk.kb.server.service.zifra;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface ZifraMessageHandler {

    boolean handleConsumerRecord(ConsumerRecord<Object, Object> consumerRecord);

}
