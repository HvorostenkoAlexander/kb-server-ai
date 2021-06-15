package com.nlmk.kb.server.service.impl;

import com.nlmk.kb.server.entity.CcmAttestationRequestMessage;
import com.nlmk.kb.server.repository.CcmMessageRepository;
import com.nlmk.kb.server.service.CcmMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CcmMessageServiceImpl implements CcmMessageService {

    private final CcmMessageRepository messageRepository;

    @Override
    public Optional<CcmAttestationRequestMessage> save(CcmAttestationRequestMessage ccmMessage) {

        if (messageRepository.existsByOffsetAndPartition(ccmMessage.getOffset(),ccmMessage.getPartition())){
            log.info("--- the message with offset: {}; partition: {} is already present in the database. message key: {} ",
                    ccmMessage.getOffset(),ccmMessage.getPartition(),ccmMessage.getKey());

            List<CcmAttestationRequestMessage> storedRequests = messageRepository.findCcmAttestationRequestMessageByOffsetAndPartition(ccmMessage.getOffset(),ccmMessage.getPartition());
            if (storedRequests.size()>1){
                log.info("--- ВНИМАНИЕ! В базе данных kb-server больше одного запроса на аттестацию  с характеристиками topic: {}," +
                                " partition: {}," +
                                " offset: {}",
                        ccmMessage.getTopic(),
                        ccmMessage.getPartition(),
                        ccmMessage.getOffset()
                );
            }

            return Optional.of(storedRequests.get(0));
        }

        ccmMessage = messageRepository.save(ccmMessage);
        log.info("--- Successfully saved ccmMessage with attestation request.primeId: {}",ccmMessage.getPrimeId());

        return Optional.ofNullable(ccmMessage);
    }

    @Override
    public Page<CcmAttestationRequestMessage> findAll(PageRequest of) {
        return messageRepository.findAll(of);
    }

    @Override
    public List<CcmAttestationRequestMessage> findByPrimeId(String primeId) {
        return messageRepository.findCcmAttestationRequestMessageByPrimeId(primeId);
    }
}
