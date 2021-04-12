package com.nlmk.kb.server.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SupService {

    @KafkaListener(topics = "${spring.kafka.template.default-topic}")
    public void receiveMessage(@Payload String supIntegralParameters) {

        log.info("--- received integralParameters: {}", supIntegralParameters);
    }
}
