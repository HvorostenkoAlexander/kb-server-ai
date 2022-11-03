package com.nlmk.kb.server.exception;

/**
 * Класс исключения для ошибок обработки сообщений Kafka
 */
public class KafkaMessageProcessingException extends RuntimeException {

    public KafkaMessageProcessingException(String message) {
        super(message);
    }

}
