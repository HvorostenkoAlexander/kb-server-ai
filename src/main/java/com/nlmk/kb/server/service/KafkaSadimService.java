package com.nlmk.kb.server.service;

import lombok.extern.slf4j.Slf4j;
import nlmk.sadim.Sadim;
import org.apache.kafka.clients.consumer.ConsumerRecord;
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
    public void receiveMessageReq(@Payload ConsumerRecord consumerRecord) {
        try {
            Sadim sadim = (Sadim) consumerRecord.value();
            log.info("SADIM data from topic: {}",consumerRecord);
        } catch (ClassCastException cce){
            log.error("cce to example: "+cce);
            log.info("SADIM data from topic: {}",consumerRecord);
        }
    }
}
