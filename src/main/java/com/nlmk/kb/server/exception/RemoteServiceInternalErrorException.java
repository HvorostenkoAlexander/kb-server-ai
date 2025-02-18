package com.nlmk.kb.server.exception;

public class RemoteServiceInternalErrorException extends RuntimeException {
    public RemoteServiceInternalErrorException(String message) {
        super(message);
    }
}
