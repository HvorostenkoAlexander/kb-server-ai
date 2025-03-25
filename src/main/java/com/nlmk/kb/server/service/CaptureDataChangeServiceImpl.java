package com.nlmk.kb.server.service;

import com.nlmk.attestation.product.api.kb.CdcUpdate;
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
    public void updateMdmMessage(CdcUpdate cdcUpdate) {
        var mdmMessage = mdmMessageRepository.findById(Long.parseLong(cdcUpdate.getMessageId()));
        if (mdmMessage.isPresent()) {
            mdmMessage.get().setNote(cdcUpdate.getStatus());
            mdmMessageRepository.save(mdmMessage.get());
        }
    }
}
