package com.nlmk.kb.server.exception;

/**
 * Класс исключения для ошибок отправки сообщений в PSM сервис
 */
public class PsmSenderException extends RuntimeException {

    public PsmSenderException(String message) {
        super(message);
    }

}
