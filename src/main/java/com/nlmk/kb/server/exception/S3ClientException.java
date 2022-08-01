package com.nlmk.kb.server.exception;

public class S3ClientException extends RuntimeException {

    public S3ClientException(String msg) {
        super(msg);
    }
}
