package com.nlmk.kb.server.exception;

/**
 * Класс исключения когда не найден Запрос на Аттестацию
 */
public class DataNotFoundException extends RuntimeException {

    public DataNotFoundException(String message) {
        super(message);
    }

}
