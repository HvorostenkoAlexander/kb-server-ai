package com.nlmk.kb.server.service;

import com.nlmk.kb.server.dto.PdmMessageDto;
import com.nlmk.kb.server.entity.pdm.PdmMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import java.util.Optional;

public interface PdmMessageService {

    public Optional<PdmMessage> save(PdmMessage message);

    public Optional<PdmMessage> update(PdmMessage message);

    Page<PdmMessageDto> getMessages(String topic,
                                    Boolean isPosted,
                                    String startDate,
                                    String endDate,
                                    PageRequest of);
}
