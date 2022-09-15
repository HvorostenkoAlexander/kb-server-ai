package com.nlmk.kb.server.exception;

/**
 * Класс исключения для ошибок отправки в сторонние сервисы
 */
public class RemoteServiceSenderException extends RuntimeException {

    public RemoteServiceSenderException(String message) {
        super(message);
    }

}
