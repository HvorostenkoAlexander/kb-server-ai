package com.nlmk.kb.server.exception;

/**
 * Класс исключения для ошибок обработки запросов на Аттестацию через REST
 */
public class CcmRequestProcessingException extends RuntimeException {

    public CcmRequestProcessingException(String message) {
        super(message);
    }

}
