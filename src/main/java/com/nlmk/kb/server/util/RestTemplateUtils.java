package com.nlmk.kb.server.util;

import org.springframework.http.HttpHeaders;

import java.util.UUID;

public class RestTemplateUtils {


    public static HttpHeaders prepareHeaders(String authHeader) {
        return prepareHeaders(authHeader, UUID.randomUUID().toString());
    }
    
    public static HttpHeaders prepareHeaders(String authHeader, String reuestId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Request-ID", "kb-" + (reuestId != null ? reuestId : UUID.randomUUID().toString()));
        if (authHeader != null) {
            headers.add(HttpHeaders.AUTHORIZATION, authHeader);
        }
        return headers;
    }
}
