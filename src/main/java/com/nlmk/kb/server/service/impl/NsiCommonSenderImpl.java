package com.nlmk.kb.server.service.impl;

import com.nlmk.kb.server.service.NsiCommonSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class NsiCommonSenderImpl implements NsiCommonSender {

    private final RestTemplate restTemplate;
    private final String URL_NSI_DICTIONARY;

    public NsiCommonSenderImpl(RestTemplate restTemplate,
                               @Value("${nsi.url.dict}") String URL_NSI_DICTIONARY) {
        this.restTemplate = restTemplate;
        this.URL_NSI_DICTIONARY = URL_NSI_DICTIONARY;
    }

    @Override
    public ResponseEntity<Long> exchange(HttpEntity<?> request,
                                         final String url_dictionary,
                                         final String operation
    ) {
        ResponseEntity<Long> response = new ResponseEntity<>(0L, HttpStatus.BAD_REQUEST);

        switch (operation) {
            case "I": {
                log.debug("--- post to NSI: " + request);
                response = restTemplate
                        .exchange(URL_NSI_DICTIONARY + url_dictionary,
                                HttpMethod.POST,
                                request,
                                Long.class);
                break;
            }
            case "U": {
                log.debug("--- put to NSI: " + request);
                response = restTemplate
                        .exchange(URL_NSI_DICTIONARY + url_dictionary,
                                HttpMethod.PUT,
                                request,
                                Long.class);
                break;
            }
            case "D": {
                log.info("--- delete from NSI: " + request);
                response = restTemplate
                        .exchange(URL_NSI_DICTIONARY + url_dictionary,
                                HttpMethod.DELETE,
                                request,
                                Long.class);
                log.info("response from NSI: " + response);
                break;
            }
            default: {
                throw new IllegalArgumentException("not supported operation: " + operation);
            }
        }
        return response;
    }
}
