package com.nlmk.kb.server.exception;

/**
 * Исключение, при ошибках настройки Kafka-Rest.
 */
public class KafkaRestConfigException extends RuntimeException {

    public KafkaRestConfigException(String message) {
        super(message);
    }

}
