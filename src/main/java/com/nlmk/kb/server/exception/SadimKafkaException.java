package com.nlmk.kb.server.exception;

/**
 * Класс исключения для общих ошибок обработки сообщения Kafka для САДиМ
 */
public class SadimKafkaException extends RuntimeException {

    public SadimKafkaException(String message) {
        super(message);
    }

}
