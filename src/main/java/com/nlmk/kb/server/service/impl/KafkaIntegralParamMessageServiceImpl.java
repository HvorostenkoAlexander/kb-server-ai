package com.nlmk.kb.server.service.impl;

import com.nlmk.kb.server.entity.KafkaIntegralParamMessage;
import com.nlmk.kb.server.repository.KafkaIntegralParamMessageRepository;
import com.nlmk.kb.server.service.KafkaIntegralParamMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaIntegralParamMessageServiceImpl implements KafkaIntegralParamMessageService {

    private final KafkaIntegralParamMessageRepository messageRepository;

    @Override
    public void messageProcessing(KafkaIntegralParamMessage message) {

        if (message==null) {
            log.info("--- invalid message: null");
        }

        if (messageRepository.existsByOffsetAndPartition(message.getOffset(), message.getPartition())) {
            log.info("--- the message with offset = {} is already present in the database. message key: {} ",
                    message.getOffset(), message.getKey());
            return;
        }

        messageRepository.save(message);
        log.info("--- Successfully saved message: {}", message);
    }
}
