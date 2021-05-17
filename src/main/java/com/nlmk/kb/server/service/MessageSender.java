package com.nlmk.kb.server.service;

import com.nlmk.kb.server.entity.pdm.PdmMessage;
import org.springframework.http.ResponseEntity;

public interface MessageSender {

    public ResponseEntity<Long> send(PdmMessage message);
    public String getType();
}
