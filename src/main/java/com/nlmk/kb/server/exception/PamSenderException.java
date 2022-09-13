package com.nlmk.kb.server.exception;

/**
 * Класс исключения для ошибок отправки сообщений в PAM сервис
 */
public class PamSenderException extends RuntimeException {

    public PamSenderException(String message) {
        super(message);
    }

}
