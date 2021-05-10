package com.nlmk.kb.server.service;

import lombok.extern.slf4j.Slf4j;
import nlmk.pdm.SpEquivalents;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("prod")
public class PdmService {

    @KafkaListener(containerFactory = "kafkaListenerContainerFactoryPdm",
            topicPartitions = {
//                    @TopicPartition(topic = "000-1.l3-pdm.cdc.sp-asap-chemical-properties.0",
//                            partitionOffsets = @PartitionOffset(partition = "0", initialOffset = "0")),
                    @TopicPartition(topic = "000-1.l3-pdm.cdc.sp-equivalents.0",
                            partitionOffsets = @PartitionOffset(partition = "0", initialOffset = "0"))
            }
    )
    public void receiveMessageReq(
                                   @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                                   @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                                   @Header(KafkaHeaders.OFFSET) int offset,
                                   @Header(KafkaHeaders.RECEIVED_TIMESTAMP) String timestamp,
                                   @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                   @Payload SpEquivalents request) {

        log.info("--- PDM received message: Key: {} ; Timestamp: {};partition {}; offset: {}; topic: {}; value:{}",
                key, timestamp, partition, offset, topic,
                request);
    }
}
