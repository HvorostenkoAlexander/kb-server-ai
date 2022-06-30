package com.nlmk.kb.server.service.sender;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

public interface NsiSender {

    ResponseEntity<Long> exchange(HttpEntity<?> request,
                                         final String url_dictionary,
                                         final String operation
    );
}
