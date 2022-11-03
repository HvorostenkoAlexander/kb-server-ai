package com.nlmk.kb.server.exception;

/**
 * Класс исключения для ошибок отправки результатов Аттестации
 */
public class AttestationResultSenderException extends RuntimeException {

    public AttestationResultSenderException(String message) {
        super(message);
    }

    public AttestationResultSenderException(Throwable cause) {
        super(cause);
    }

}
