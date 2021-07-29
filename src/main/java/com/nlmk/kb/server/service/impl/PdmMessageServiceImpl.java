package com.nlmk.kb.server.service.impl;

import com.nlmk.kb.server.entity.pdm.PdmMessage;
import com.nlmk.kb.server.repository.PdmMessageRepository;
import com.nlmk.kb.server.service.PdmMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdmMessageServiceImpl implements PdmMessageService {

    private final PdmMessageRepository messageRepository;

    @Override
    @Transactional
    public Optional<PdmMessage> save(PdmMessage message) {
        Assert.notNull(message, "PdmMessage for saving is null.");

        if (messageRepository.existsByTopicAndOffsetAndPartition(
                message.getTopic(), message.getOffset(), message.getPartition())) {
            log.info("The message from " +
                            "topic: [{}], " +
                            "partition: [{}], " +
                            "offset: [{}] is already present in the database. " +
                            "message key: {} ",
                    message.getTopic(),
                    message.getPartition(),
                    message.getOffset(),
                    message.getKey());

            List<PdmMessage> storedMessages = messageRepository.findByTopicAndOffsetAndPartition(
                    message.getTopic(),
                    message.getOffset(),
                    message.getPartition()
            );

            if (storedMessages.size() > 1) {
                log.warn("ВНИМАНИЕ! В базе данных kb-server больше одного сообщения с характеристиками" +
                                " topic: {}," +
                                " partition: {}," +
                                " offset: {}",
                        message.getTopic(),
                        message.getPartition(),
                        message.getOffset()
                );
            }
            return Optional.of(storedMessages.get(0));
        }

        messageRepository.save(message);

        log.info("Successfully saved message from PDM:partition:{}; offset: {}; topic: {}, key:{};",
                message.getPartition(),
                message.getOffset(),
                message.getTopic(),
                message.getKey());

        return Optional.of(message);
    }

    @Override
    public Optional<PdmMessage> update(PdmMessage message) {
        Assert.notNull(message, "PdmMessage for update is null.");

        return Optional.of(messageRepository.save(message));
    }
}
