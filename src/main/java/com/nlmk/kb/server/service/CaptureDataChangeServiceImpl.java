package com.nlmk.kb.server.service;

import com.nlmk.kb.server.repository.MdmMessageRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class CaptureDataChangeServiceImpl implements CaptureDataChangeService {

    private final MdmMessageRepository mdmMessageRepository;
    @Override
    public void updateMdmMessage(String messageId, String status) {
        var mdmMessage = mdmMessageRepository.findById(Long.parseLong(messageId));
        if (mdmMessage.isPresent()) {
            mdmMessage.get().setNote(status);
            mdmMessageRepository.save(mdmMessage.get());
        }
    }
}
