package com.nlmk.kb.server.service.pdm.senders;

import com.nlmk.kb.server.entity.pdm.PdmMessage;

public interface PdmMessageSender {

    Long send(PdmMessage message);

    String getType();

}
