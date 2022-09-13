package com.nlmk.kb.server.exception;

/**
 * Класс исключения для ошибок отправки сообщений в NSI сервис
 */
public class NsiSenderException extends RuntimeException {

    public NsiSenderException(String message) {
        super(message);
    }

}
