package com.nlmk.kb.server.util;

import com.nlmk.kb.server.config.KbConstants;
import org.springframework.http.HttpHeaders;

import java.util.Objects;
import java.util.UUID;

public class RestTemplateUtils {

    private RestTemplateUtils() {
        throw new IllegalStateException("Utils");
    }

    public static HttpHeaders prepareHeaders(String requestId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(KbConstants.REQUEST_ID_HEADER,
                // готовый requestId или новый с префиксом
                Objects.requireNonNullElseGet(requestId, () -> KbConstants.DEFAULT_PREFIX + UUID.randomUUID()));
        return headers;
    }

}
