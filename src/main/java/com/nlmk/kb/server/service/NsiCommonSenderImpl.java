package com.nlmk.kb.server.service;

import com.nlmk.kb.server.service.NsiCommonSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
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
                log.info("post to NSI: " + request);
                response = restTemplate
                        .exchange(URL_NSI_DICTIONARY + url_dictionary,
                                HttpMethod.POST,
                                request,
                                Long.class);
                log.info("operation: [{}]; response from NSI: [{}], request: [{}]", operation, response, request);
                break;
            }
            case "U": {
                log.info("put to NSI: " + request);
                response = restTemplate
                        .exchange(URL_NSI_DICTIONARY + url_dictionary,
                                HttpMethod.PUT,
                                request,
                                Long.class);
                log.info("operation: [{}]; response from NSI: [{}], request: [{}]", operation, response, request);
                break;
            }
            case "D": {
                log.info("delete from NSI: " + request);
                try {
                    response = restTemplate
                            .exchange(URL_NSI_DICTIONARY + url_dictionary,
                                    HttpMethod.DELETE,
                                    request,
                                    Long.class);
                    log.info("operation: [{}]; response from NSI: [{}], request: [{}]", operation, response, request);
                } catch (HttpClientErrorException hcee) {
                    if (hcee.getRawStatusCode() == 404) {
                        response = new ResponseEntity<>(0L, HttpStatus.NOT_FOUND);
                        log.warn("response from NSI: [{}], request: [{}]", response, request);

                        return response;
                    }
                    throw hcee;
                }
                break;
            }
            default: {
                throw new IllegalArgumentException("not supported operation: " + operation);
            }
        }
        return response;
    }
}
