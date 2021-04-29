package com.nlmk.kb.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nlmk.l3.ccm.pgp.AttestationRequest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CcmService {

            @KafkaListener(containerFactory = "kafkaListenerContainerFactoryReq",
                topicPartitions = {@TopicPartition(topic = "${kafka.ccm.topicReq}",
                partitionOffsets =
                @PartitionOffset(partition = "0", initialOffset = "0")),})
    public void receiveMessageReq(@Payload AttestationRequest request){
        log.info("--- receiveMessageReq from CCM AttestationRequest: "+ request);
    }
}
