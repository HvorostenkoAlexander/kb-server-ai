package com.nlmk.kb.server.service.impl;

import com.nlmk.attestation.zorder.ZORDERS051;
import com.nlmk.kb.server.entity.SapMessage;
import com.nlmk.kb.server.entity.SapMessageState;
import com.nlmk.kb.server.exception.S3ClientException;
import com.nlmk.kb.server.repository.SapMessageRepository;
import com.nlmk.kb.server.service.*;
import com.nlmk.s3.proxy.s3notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SapMessageHandlerImpl implements SapMessageHandler {

    private final SapMessageRepository repository;
    private final S3Service s3Service;
    private final PsmSender psmSender;

    private static final String MSG_TEMPLATE = "handleConsumerRecord. {}";

    @Override
    public boolean handleConsumerRecord(final ConsumerRecord<Object, Object> record) {
        log.debug("handleConsumerRecord. record: [{}]", record);

        s3notification notification = (s3notification) record.value();

        Optional<SapMessage> storedMessage =
                repository.findFirstByTopicAndOffsetAndPartition(record.topic(), record.offset(), record.partition());

        if (storedMessage.isPresent() && List.of(SapMessageState.DONE, SapMessageState.ERROR).contains(storedMessage.get().getState())) {
            log.warn("handleConsumerRecord. Сообщение SAP topic: [{}], partition: [{}], offset: [{}] уже было обработано со статусом [{}].",
                    record.topic(), record.partition(), record.offset(), storedMessage.get().getState());
            return true;
        }

        SapMessage message = storedMessage.orElseGet(
                () -> repository.save(SapMessage.builder()
                        .topic(record.topic())
                        .partition(record.partition())
                        .offset(record.offset())
                        .key((String) record.key())
                        .bucket(notification.getBucket().toString())
                        .path(notification.getPath().toString())
                        .processorVersion(notification.getProcessorVersion().toString())
                        .server(notification.getServer().toString())
                        .ts(new Date(Long.parseLong(notification.getTs().toString())))
                        .state(SapMessageState.NEW)
                        .build()));

        if (StringUtils.isBlank(message.getOrder())) {
            try {
                String xmlOrder = s3Service.getObjectAsString(message.getBucket(), message.getPath());
                message.setOrder(xmlOrder);
                repository.save(message);
            } catch (S3ClientException e) {
                log.error(MSG_TEMPLATE, e.getMessage());
                return false;
            }
        }

        ZORDERS051 zorder;

        try {
            zorder = s3Service.getZorder(message.getOrder());
        } catch (S3ClientException e) {
            log.error(MSG_TEMPLATE, e.getMessage());
            message.setState(SapMessageState.ERROR);
            repository.save(message);
            return true;
        }

        try {
            psmSender.postZorder(zorder);
            message.setState(SapMessageState.DONE);
            repository.save(message);
            log.info("handleConsumerRecord. Sent to PSM: [{}]", message.getPath());
            return true;
        } catch (Exception e) {
            log.error(MSG_TEMPLATE, e.getMessage());
            return false;
        }
    }
}
