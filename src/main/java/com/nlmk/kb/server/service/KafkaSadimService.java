package com.nlmk.kb.server.service;

import com.nlmk.kb.server.entity.PreAttestationParam;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaSadimService {

    private final SadimMessageService messageService;

    @KafkaListener(containerFactory = "kafkaListenerSadim", topics = {"${kafka.sadim.topic}"})
    @Timed(value="kafka_listener", percentiles = {0.99, 0.95})
    public void receiveMessageReq(@Payload ConsumerRecord consumerRecord) {

        log.info("--- SADIM message with offset: {};", consumerRecord.offset());

        val sadimMessage = messageService.saveMessage(consumerRecord);
        log.info("--- saved SADIM massage: {}",sadimMessage);
    }
}
