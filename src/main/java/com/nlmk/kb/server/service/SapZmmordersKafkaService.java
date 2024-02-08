package com.nlmk.kb.server.service;

import com.nlmk.kb.server.exception.DateTimeParseException;
import com.nlmk.kb.server.exception.KafkaMessageProcessingException;
import com.nlmk.kb.server.service.sap.SapMessageHandler;
import com.nlmk.s3.proxy.s3notification;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SapZmmordersKafkaService {
    private final long sleepTime;
    private final SapMessageHandler sapMessageHandler;

    public SapZmmordersKafkaService(@Value("${kafka.ack.nack.sleep-time}") long sleepTime,
                           SapMessageHandler sapMessageHandler) {
        this.sleepTime = sleepTime;
        this.sapMessageHandler = sapMessageHandler;
    }

    @KafkaListener(containerFactory = "sapZmmordersKafkaListenerContainerFactory",
            topics = {"${kafka.sap.topic.s3.zmmordersdop}"}
    )
    @Timed(value = "kafka_listener", percentiles = {0.99, 0.95})
    public void receiveMessageReq(@Payload ConsumerRecord<String, s3notification> consumerRecord,
                                  Acknowledgment ack) {
        log.info("receiveZmmordersMessageReq (SAP): topic [{}], partition [{}], offset [{}], key [{}]",
                consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset(), consumerRecord.key());

        try {
            if (sapMessageHandler.handleConsumerRecord(consumerRecord)) {
                ack.acknowledge();
                log.debug("receiveZmmordersMessageReq (SAP): Sending ack for topic [{}], partition [{}], offset [{}], key [{}]",
                        consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset(), consumerRecord.key());
            } else {
                log.debug("receiveZmmordersMessageReq (SAP): Sending nack for topic [{}], partition [{}], offset [{}], key [{}]",
                        consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset(), consumerRecord.key());
                ack.nack(sleepTime);
            }
        } catch (DateTimeParseException e) {
            log.warn("receiveZmmordersMessageReq, DateTimeParseException", e);
            ack.acknowledge();
            throw new DateTimeParseException("переброс: " + e);
        } catch (Exception e) {
            log.warn("receiveZmmordersMessageReq, Exception", e);
            ack.nack(sleepTime);
            throw new KafkaMessageProcessingException("переброс: " + e);
        }
    }
}
