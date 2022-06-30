package com.nlmk.kb.server.service.pdm;

import com.nlmk.kb.server.dto.PdmMessageDto;
import com.nlmk.kb.server.entity.pdm.PdmMessage;
import com.nlmk.kb.server.repository.PdmMessageRepository;
import com.nlmk.kb.server.service.CommonConverter;
import com.nlmk.kb.server.service.DtoConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

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
    private final NsiClientService nsiClientService;
    private final CommonConverter converter;

    @Override
    @Transactional
    public Optional<PdmMessage> save(PdmMessage message) {
        Assert.notNull(message, "PdmMessage for saving is null.");

        if (repository.existsByTopicAndOffsetAndPartition(
                message.getTopic(), message.getOffset(), message.getPartition())) {
            log.warn("The message from " +
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

        log.debug("Successfully saved message from PDM:partition:{}; offset: {}; topic: {}, key:{};",
                message.getPartition(),
                message.getOffset(),
                message.getTopic(),
                message.getKey());

        return Optional.of(message);
    }

    @Override
    public Optional<PdmMessage> update(PdmMessage message) {
        log.debug("update PdmMessage: [{}]", message);

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

    @Override
    public ResponseEntity<Long> resendingToNsi(Long id) {
        Assert.notNull(id, "id не должен быть null");

        final var message = repository.findById(id).orElseThrow(
                () -> new IllegalArgumentException(
                        String.format("Не найден объект с id: [%s]", id)
                )
        );

        return sendToNsi(message);
    }

    @Override
    public ResponseEntity<Long> sendToNsi(PdmMessage message) {
        ResponseEntity<Long> response = nsiClientService.sendPdmMessage(message);
        setStatusMessage(message, response.getStatusCode());
        final var updated = update(message);

        return response;
    }

    private String toStringByPattern(Date date, String pattern) {
        return converter.parseToStringByDatePattern(date,pattern);
    }

    private void setStatusMessage(PdmMessage message, HttpStatus status) {
        log.debug("setStatusMessage; HttpStatus: [{}], PdmMessage:[{}]", status.toString(), message);

        if (status == HttpStatus.ACCEPTED ||
                status == HttpStatus.NOT_FOUND ||
                status == HttpStatus.OK) {
            message.setPosted(true);
            message.setKbReceiptTs(new Date());
            message.setNote(status.toString());
        } else {
            message.setPosted(false);
            message.setKbReceiptTs(new Date());
            message.setNote(status.toString());
        }
    }
}
