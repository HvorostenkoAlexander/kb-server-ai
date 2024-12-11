package com.nlmk.kb.server.service.zifra;

import com.nlmk.kb.server.entity.mdm.MdmMessage;
import com.nlmk.kb.server.repository.MdmMessageRepository;
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
public class MdmMessageServiceImpl implements MdmMessageService {

    private final MdmMessageRepository repository;

    @Override
    @Transactional
    public Optional<MdmMessage> save(MdmMessage message) {
        Assert.notNull(message, "MdmMessage for saving is null.");

        if (repository.existsByTopicAndOffsetAndPartition(
                message.getTopic(), message.getOffset(), message.getPartition())) {
            log.warn("The message from topic: [{}], partition: [{}], offset: [{}] is already present in the database.",
                    message.getTopic(), message.getPartition(), message.getOffset());

            List<MdmMessage> storedMessages = repository.findByTopicAndOffsetAndPartition(
                    message.getTopic(),
                    message.getOffset(),
                    message.getPartition()
            );

            if (storedMessages.size() > 1) {
                log.warn("ВНИМАНИЕ! В базе данных kb-server больше одного Mdm сообщения с характеристиками topic: {}, partition: {}, offset: {}",
                        message.getTopic(), message.getPartition(), message.getOffset());
            }
            return Optional.of(storedMessages.get(0));
        }

        var result = repository.save(message);

        log.debug("Successfully saved message from MDM:partition:{}; offset: {}; topic: {};",
                message.getPartition(),
                message.getOffset(),
                message.getTopic());

        return Optional.of(result);
    }

    @Override
    public Optional<MdmMessage> update(MdmMessage message) {
        log.debug("update MdmMessage: [{}]", message);

        Assert.notNull(message, "MdmMessage for update is null.");

        return Optional.of(repository.save(message));
    }

}
