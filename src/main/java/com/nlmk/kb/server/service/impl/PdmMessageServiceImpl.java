package com.nlmk.kb.server.service.impl;

import com.nlmk.kb.server.dto.PdmMessageDto;
import com.nlmk.kb.server.entity.pdm.PdmMessage;
import com.nlmk.kb.server.repository.PdmMessageRepository;
import com.nlmk.kb.server.service.DtoConverter;
import com.nlmk.kb.server.service.PdmMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdmMessageServiceImpl implements PdmMessageService {

    private final PdmMessageRepository repository;
    private final DtoConverter dtoConverter;

    @Override
    @Transactional
    public Optional<PdmMessage> save(PdmMessage message) {
        Assert.notNull(message, "PdmMessage for saving is null.");

        if (repository.existsByTopicAndOffsetAndPartition(
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

            List<PdmMessage> storedMessages = repository.findByTopicAndOffsetAndPartition(
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

        repository.save(message);

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

        return Optional.of(repository.save(message));
    }

    @Override
    public Page<PdmMessageDto> getMessages(String topic,
                                           Boolean isPosted,
                                           Date startDate,
                                           Date endDate,
                                           PageRequest of) {
        Assert.notNull(topic, "Название топика не должно быть null");
        Assert.notNull(of, "PageRequest не должен быть null");

        return repository.getMessages(
                topic,
                isPosted,
                toStringByPattern(startDate, "yyyy-MM-dd"),
                toStringByPattern(endDate, "yyyy-MM-dd"),
                of
        ).map(dtoConverter::toPdmMessageDto);
    }

    @Override
    public List<PdmMessageDto> getMessagesByOffset(String topic, Integer partition, Long offset) {
        Assert.notNull(topic, "topic не должен быть null");
        Assert.notNull(topic, "partition не должен быть null");
        Assert.notNull(topic, "offset не должен быть null");

        return repository.findByTopicAndOffsetAndPartition(topic, offset, partition)
                .stream().map(dtoConverter::toPdmMessageDto).collect(Collectors.toList());
    }

    @Override
    public PdmMessageDto getMessageById(Long id) {
        Assert.notNull(id, "id не должен быть null");

        return repository.findById(id).map(dtoConverter::toPdmMessageDto).orElseThrow(
                () -> new IllegalArgumentException(
                        String.format("Не найден объект с id: [%s]", id)
                )
        );
    }

    @Override
    public Long deleteMessageById(Long id) {
        Assert.notNull(id, "id не должен быть null");
        repository.deleteById(id);

        return id;
    }

    private String toStringByPattern(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        DateFormat df = new SimpleDateFormat(pattern);
        String s = df.format(date);
        log.info("--- date :" + s);
        return s;
    }
}
