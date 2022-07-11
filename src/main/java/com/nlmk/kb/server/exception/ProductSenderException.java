package com.nlmk.kb.server.exception;

/**
 * Класс исключения для ошибок отправки результатов Аттестации
 */
public class ProductSenderException extends RuntimeException {

    public ProductSenderException(String message) {
        super(message);
    }

    public ProductSenderException(Throwable cause) {
        super(cause);
    }

}
