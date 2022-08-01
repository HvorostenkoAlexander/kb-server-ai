package com.nlmk.kb.server.service.pdm.senders;

import com.nlmk.kb.server.entity.pdm.PdmMessage;
import org.springframework.http.ResponseEntity;

public interface MessageSender {

    ResponseEntity<Long> send(PdmMessage message);

    String getType();
}
