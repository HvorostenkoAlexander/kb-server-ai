package com.nlmk.kb.server.service.sap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nlmk.attestation.zmmorder.ZMMORDERS05DOP;
import com.nlmk.attestation.zorder.ZORDERS051;
import com.nlmk.kb.server.entity.SapMessage;
import com.nlmk.kb.server.entity.SapMessageState;
import com.nlmk.kb.server.exception.RemoteServiceInternalErrorException;
import com.nlmk.kb.server.exception.RemoteServiceTimeoutException;
import com.nlmk.kb.server.exception.S3ClientException;
import com.nlmk.kb.server.repository.SapMessageRepository;
import com.nlmk.kb.server.service.sender.PsmSender;
import com.nlmk.s3.proxy.s3notification;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class SapMessageHandlerImpl implements SapMessageHandler {

    private final SapMessageRepository repository;
    private final S3Service s3Service;
    private final PsmSender psmSender;

    private final String idoczordrsBucketName;

    public SapMessageHandlerImpl(SapMessageRepository repository, S3Service s3Service, PsmSender psmSender,
                                 @Value("${s3.idoczordrs.bucket-name}") String idoczordrsBucketName) {
        this.repository = repository;
        this.s3Service = s3Service;
        this.psmSender = psmSender;
        this.idoczordrsBucketName = idoczordrsBucketName;
    }

    private static final String MSG_TEMPLATE = "handleConsumerRecord. {}";

    @Override
    public boolean handleConsumerRecord(final ConsumerRecord<String, s3notification> consumerRecord) {
        log.info("handleConsumerRecord (SAP): topic [{}], partition [{}], offset [{}], key [{}]",
                consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset(), consumerRecord.key());
        log.debug("handleConsumerRecord (SAP): record: [{}]", consumerRecord);

        s3notification notification = consumerRecord.value();

        Optional<SapMessage> storedMessage =
                repository.findFirstByTopicAndOffsetAndPartition(consumerRecord.topic(), consumerRecord.offset(), consumerRecord.partition());

        if (storedMessage.isPresent()
                && List.of(SapMessageState.DONE, SapMessageState.ERROR).contains(storedMessage.get().getState())) {
            log.warn("handleConsumerRecord (SAP): Сообщение SAP topic: [{}], partition: [{}], offset: [{}] уже было обработано со статусом [{}].",
                    consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset(), storedMessage.get().getState());
            return true;
        }

        SapMessage message = storedMessage.orElseGet(() -> {
            log.debug("handleConsumerRecord (SAP): Сообщение SAP topic: [{}], partition: [{}], offset: [{}] ещё не было обработано. Сохраняем в БД.",
                    consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset());
            return repository.save(SapMessage.builder()
                    .topic(consumerRecord.topic())
                    .partition(consumerRecord.partition())
                    .offset(consumerRecord.offset())
                    .key(consumerRecord.key())
                    .bucket(notification.getBucket().toString())
                    .path(notification.getPath().toString())
                    .processorVersion(notification.getProcessorVersion().toString())
                    .server(notification.getServer().toString())
                    .ts(new Date(Long.parseLong(notification.getTs().toString())))
                    .state(SapMessageState.NEW)
                    .build());
        });

        if (StringUtils.isBlank(message.getOrder())) {
            try {
                log.info("handleConsumerRecord (SAP): Сообщение SAP topic: [{}], partition: [{}], offset: [{}] не содержит заказ. Пробуем получить из S3: bucket: [{}], path: [{}]",
                        consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset(), message.getBucket(), message.getPath());
                String xmlOrder = s3Service.getObjectAsStringFromBucket(message.getBucket(), message.getPath());
                message.setOrder(xmlOrder);
                repository.save(message);
            } catch (S3ClientException e) {
                log.error(MSG_TEMPLATE, e.getMessage());
                return false;
            }
        }

        boolean isZorder = message.getBucket().equals(idoczordrsBucketName);
        try {
            if (isZorder) {
                log.info("handleConsumerRecord (SAP): Заказ из сообщения SAP topic: [{}], partition: [{}], offset: [{}] является ZORDERS051.",
                        consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset());
                ZORDERS051 zorder = s3Service.unmarshalZorder(message.getOrder());
                psmSender.postZorder(zorder);
                message.setOrderNum(zorder.getIDOC().getE1EDK01().getBELNR());
            } else {
                log.info("handleConsumerRecord (SAP): Заказ из сообщения SAP topic: [{}], partition: [{}], offset: [{}] является ZMMORDERS05DOP.",
                        consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset());
                ZMMORDERS05DOP zmmorder = s3Service.unmarshalZmmorder(message.getOrder());
                psmSender.postZmmorder(zmmorder);
                message.setOrderNum(zmmorder.getIDOC().getE1EDK01().getBELNR());
            }
        } catch (S3ClientException | RemoteServiceInternalErrorException | RemoteServiceTimeoutException e) {
            log.error(MSG_TEMPLATE, e.getMessage());
            message.setState(SapMessageState.ERROR);
            repository.save(message);
            return true;
        } catch (JsonProcessingException e) {
            log.error(MSG_TEMPLATE, e.getMessage());
            return false;
        }
        message.setState(SapMessageState.DONE);
        repository.save(message);
        log.info("handleConsumerRecord. Sent to PSM: [{}]", message.getPath());
        return true;
    }
}
