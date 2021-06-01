package com.nlmk.kb.server.service.impl;

import com.nlmk.kb.server.entity.pdm.PdmMessage;
import com.nlmk.kb.server.repository.PdmMessageRepository;
import com.nlmk.kb.server.service.PdmMessageConverter;
import com.nlmk.kb.server.service.PdmMessageService;
import com.nlmk.kb.server.util.PdmConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdmMessageServiceImpl implements PdmMessageService {

    private final PdmMessageRepository messageRepository;

    @Override
    public Optional<PdmMessage> save(PdmMessage message) {
        Assert.notNull(message,"PdmMessage for saving is null.");

        if (messageRepository.existsByTopicAndOffsetAndPartition(
                message.getTopic(), message.getOffset(), message.getPartition())) {
            log.debug("--- the message with offset: {} from topic: {} is already present in the database. message key: {} ",
                    message.getOffset(), message.getTopic(), message.getKey());

            // return Optional.empty();//todo закомментированно с цель проверки работы алгоритмов передачи в nsi-server, как будет проверено ВЕРНУТЬ!
            return Optional.of(messageRepository.findByTopicAndOffsetAndPartition(message.getTopic(), message.getOffset(), message.getPartition()));
        }

        messageRepository.save(message);

        log.debug("--- Successfully saved message from PDM:partition:{}; offset: {}; topic: {}, key:{};",
                message.getPartition(),
                message.getOffset(),
                message.getTopic(),
                message.getKey());

        return Optional.of(message);
    }

    @Override
    public Optional<PdmMessage> update(PdmMessage message) {
        Assert.notNull(message,"PdmMessage for update is null.");

        return Optional.of(messageRepository.save(message));
    }
}
