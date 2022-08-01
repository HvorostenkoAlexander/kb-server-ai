package com.nlmk.kb.server.service;

import com.nlmk.kb.server.exception.DateTimeParseException;
import com.nlmk.kb.server.exception.SapKafkaException;
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
public class SapKafkaService {

    private final long sleepTime;
    private final SapMessageHandler sapMessageHandler;

    public SapKafkaService(@Value("${kafka.ack.nack.sleep-time}") long sleepTime,
                           SapMessageHandler sapMessageHandler) {
        this.sleepTime = sleepTime;
        this.sapMessageHandler = sapMessageHandler;

    }

    @KafkaListener(containerFactory = "sapKafkaListenerContainerFactory",
            topics = {"${kafka.sap.topic.s3.idoczordrs}"}
    )
    @Timed(value = "kafka_listener", percentiles = {0.99, 0.95})
    public void receiveMessageReq(@Payload ConsumerRecord<String, s3notification> request, Acknowledgment ack) {
        log.info("SAP consumer record: topic: {}; partition: {}; offset: {}, key: {}",
                request.topic(),
                request.partition(),
                request.offset(),
                request.key()
        );

        try {
            if (sapMessageHandler.handleConsumerRecord(request)) {
                ack.acknowledge();
            } else {
                ack.nack(sleepTime);
            }
        } catch (DateTimeParseException e) {
            log.warn("receiveMessageReq, DateTimeParseException", e);
            ack.acknowledge();
            throw new DateTimeParseException("переброс: " + e);
        } catch (Exception e) {
            log.warn("receiveMessageReq, Exception", e);
            ack.nack(sleepTime);
            throw new SapKafkaException("переброс: " + e);
        }
    }

}
