package com.nlmk.kb.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class KeycloakAuthorizationException extends RuntimeException {

    public KeycloakAuthorizationException(String message) {
        super(message);
    }
}
