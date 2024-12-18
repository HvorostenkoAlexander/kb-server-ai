package com.nlmk.kb.server.service.zifra;

import com.nlmk.kb.server.entity.mdm.MdmMessage;

public interface MdmMessageSender {

    Long send(MdmMessage message);

    String getType();

}
