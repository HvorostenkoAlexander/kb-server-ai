package com.nlmk.kb.server.exception;

public class RemoteServiceTimeoutException extends RuntimeException {
    public RemoteServiceTimeoutException(String message) {
        super(message);
    }
}
