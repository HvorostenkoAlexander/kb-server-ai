package com.nlmk.kb.server.exception;

/**
 * Класс исключения для ошибок обработки запросов на Аттестацию через REST
 */
public class RequestProcessingException extends RuntimeException {

    public RequestProcessingException(String message) {
        super(message);
    }

}
