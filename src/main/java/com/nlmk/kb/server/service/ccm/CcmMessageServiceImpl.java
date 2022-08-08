package com.nlmk.kb.server.service.ccm;

import com.nlmk.kb.server.entity.CcmMessage;
import com.nlmk.kb.server.repository.CcmMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CcmMessageServiceImpl implements CcmMessageService {

    private final CcmMessageRepository messageRepository;

    @Override
    @Transactional
    public Optional<CcmMessage> save(CcmMessage ccmMessage) {

        if (messageRepository.existsByTopicAndPartitionAndOffset(ccmMessage.getTopic(),
                ccmMessage.getPartition(),
                ccmMessage.getOffset())
        ) {
            log.info("the message with topic: [{}]; partition: {}; offset: {} is already present in the database. ",
                    ccmMessage.getTopic(), ccmMessage.getPartition(), ccmMessage.getOffset());

            List<CcmMessage> storedRequests = messageRepository
                    .findByTopicAndPartitionAndOffset(
                            ccmMessage.getTopic(),
                            ccmMessage.getPartition(),
                            ccmMessage.getOffset()
                    );
            if (storedRequests.size() > 1) {
                log.warn("ВНИМАНИЕ! В базе данных kb-server больше одного запроса на аттестацию с характеристиками" +
                                " topic: {}," +
                                " partition: {}," +
                                " offset: {}",
                        ccmMessage.getTopic(),
                        ccmMessage.getPartition(),
                        ccmMessage.getOffset()
                );
            }

            return Optional.of(storedRequests.get(0));
        }

        messageRepository.save(ccmMessage);
        log.info("Successfully saved ccmMessage with attestation request.primeId: {}", ccmMessage.getPrimeId());

        return Optional.ofNullable(ccmMessage);
    }

    @Override
    public Page<CcmMessage> findAll(PageRequest of) {
        return messageRepository.findAll(of);
    }

    @Override
    public List<CcmMessage> findByPrimeId(String primeId) {
        return messageRepository.findByPrimeId(primeId);
    }

    @Override
    public CcmMessage update(CcmMessage ccmMessage) {
        Assert.notNull(ccmMessage, "ccmMessage must not be null");

        return messageRepository.save(ccmMessage);
    }

    @Override
    public Optional<CcmMessage> findLastMessage(String primeId) {
        return messageRepository.findFirstByPrimeIdOrderByKbReceiptTsDesc(primeId);
    }

}
