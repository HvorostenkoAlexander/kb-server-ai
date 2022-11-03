package com.nlmk.kb.server.exception;

/**
 * Класс исключения когда не найден Запрос на Аттестацию
 */
public class AttestationRequestNotFoundException extends RuntimeException {

    public AttestationRequestNotFoundException(String message) {
        super(message);
    }

}
