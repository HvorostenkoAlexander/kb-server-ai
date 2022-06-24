package com.nlmk.kb.server.service.pdm;

import com.nlmk.kb.server.dto.PdmMessageDto;
import com.nlmk.kb.server.entity.pdm.PdmMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface PdmMessageService {

    Optional<PdmMessage> save(PdmMessage message);

    Optional<PdmMessage> update(PdmMessage message);

    Page<PdmMessageDto> getMessages(String topic,
                                    Boolean isPosted,
                                    Date startDate,
                                    Date endDate,
                                    PageRequest of);

    List<PdmMessageDto> getMessagesByOffset(String topic, Integer partition, Long offset);

    PdmMessageDto getMessageById(Long id);

    Long deleteMessageById(Long id);

    ResponseEntity<Long> sendToNsi(PdmMessage pdmMessage);

    ResponseEntity<Long> resendingToNsi(Long id);

}
