package com.nlmk.kb.server.service;

import lombok.extern.slf4j.Slf4j;
import nlmk.l3.sup.IntegralParameters;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SupService {

    @KafkaListener(topics = {"${kafka.sup.topicIp}"},
            containerFactory = "kafkaListenerContainerFactory")
    public void receiveMessage(@Payload IntegralParameters supIntegralParameters) {

        log.info("--- received integralParameters: {}", supIntegralParameters);
    }
}
