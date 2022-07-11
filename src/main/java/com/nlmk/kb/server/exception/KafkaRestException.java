package com.nlmk.kb.server.exception;

/**
 * Исключение, которое выбрасывается при отправке сообщения в Kafka-Rest.
 */
public class KafkaRestException extends ProductSenderException {

    public KafkaRestException(String message) {
        super(message);
    }

    public KafkaRestException(Throwable cause) {
        super(cause);
    }

}
