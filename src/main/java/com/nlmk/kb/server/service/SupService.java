package com.nlmk.kb.server.service;

import lombok.extern.slf4j.Slf4j;
import nlmk.l3.sup.IntegralParameters;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SupService {

//    @TopicPartition(topic = "topic2", partitions = "0",
//            partitionOffsets = @PartitionOffset(partition = "1", initialOffset = "100")
//@KafkaListener(topics = {"${kafka.sup.topicIp}"},
//        containerFactory = "kafkaListenerContainerFactoryIp")


    @KafkaListener(containerFactory = "kafkaListenerContainerFactoryIp",
                    topicPartitions = {@TopicPartition(topic = "topic2", partitions = "0",
                                    partitionOffsets =
                                    @PartitionOffset(partition = "0", initialOffset = "24")),})
    public void receiveMessage(@Payload String supIntegralParameters) {

        log.info("--- received integralParameters: {}", supIntegralParameters);
    }
}
