package com.nlmk.kb.server.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

public interface NsiCommonSender {

    ResponseEntity<Long> exchange(HttpEntity<?> request,
                                         final String url_dictionary,
                                         final String operation
    );
}
