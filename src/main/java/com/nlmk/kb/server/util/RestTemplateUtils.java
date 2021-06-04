package com.nlmk.kb.server.util;

import org.springframework.http.HttpHeaders;

import java.util.UUID;

public class RestTemplateUtils {

    public static HttpHeaders prepareHeaders() {
        return prepareHeaders(UUID.randomUUID().toString());
    }
    
    public static HttpHeaders prepareHeaders(String reuestId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Request-ID", "kb-" + (reuestId != null ? reuestId : UUID.randomUUID().toString()));
        return headers;
    }
}
