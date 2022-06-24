package com.nlmk.kb.server.service.sadim;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface SadimMessageService {

    /**
     * Обработка сообщения Kafka от Садим
     *
     * @param consumerRecord объект сообщения Kafka
     * @return значение primeId или null
     */
    String saveMessage(ConsumerRecord<Object, Object> consumerRecord);

}
