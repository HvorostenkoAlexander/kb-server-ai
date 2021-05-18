package com.nlmk.kb.server.service.impl;

import com.nlmk.kb.server.entity.KafkaUnrecoverableParamMessage;
import com.nlmk.kb.server.repository.KafkaUnrecoverableParamMessageRepository;
import com.nlmk.kb.server.service.KafkaUnrecoverableParamMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaUnrecoverableParamMessageServiceImp implements KafkaUnrecoverableParamMessageService {

    private final KafkaUnrecoverableParamMessageRepository messageRepository;

    @Override
    public void messageProcessing(KafkaUnrecoverableParamMessage message) {
       if (message==null) {
            log.info("--- invalid message: null");
        }

        if (messageRepository.existsByOffsetAndPartition(message.getOffset(), message.getPartition())) {
            log.debug("--- the message with offset = {} is already present in the database. message key: {} ",
                    message.getOffset(), message.getKey());
            return;
        }

        messageRepository.save(message);
        log.debug("--- Successfully saved message: {}", message);
    }
}
