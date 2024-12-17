package com.nlmk.kb.server.service.zifra;

import com.nlmk.kb.server.entity.mdm.MdmMessage;

import java.util.Optional;

public interface MdmMessageService {

    Optional<MdmMessage> save(MdmMessage message);

    Optional<MdmMessage> update(MdmMessage message);

}
