package com.nlmk.kb.server.service.sender;

import com.nlmk.kb.server.entity.pdm.PdmOp;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

public interface NsiSender {

    ResponseEntity<Long> exchange(HttpEntity<?> request,
                                  final String urlDictionary,
                                  final PdmOp operation);

}
