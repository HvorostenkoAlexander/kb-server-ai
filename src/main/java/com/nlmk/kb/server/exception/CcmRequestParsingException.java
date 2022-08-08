package com.nlmk.kb.server.exception;

/**
 * Класс исключения для ошибок анализа сообщений на Аттестацию, поданных через REST.
 */
public class CcmRequestParsingException extends RuntimeException {

    public CcmRequestParsingException(String message) {
        super(message);
    }

}
