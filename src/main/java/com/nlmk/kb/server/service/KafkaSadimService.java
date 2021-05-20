package com.nlmk.kb.server.service;

import lombok.extern.slf4j.Slf4j;
import nlmk.sadim.Example;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaSadimService {

    @KafkaListener(containerFactory = "kafkaListenerSadim",
            topicPartitions = {@TopicPartition(topic = "PA-MU.NLMK.P3.HSM",
                    partitionOffsets =
                    @PartitionOffset(partition = "0", initialOffset = "0")),})
    public void receiveMessageReq(@Payload String jsonNode) {

        log.info(" SADIM data from topic: {}",jsonNode);
    }
}
