package com.nlmk.kb.server.service;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class KafkaSadimService {
    private  final SadimJsonParser sadimJsonParser;

    public KafkaSadimService(@Qualifier("sadimStreamApiParser") SadimJsonParser sadimJsonParser) {
        this.sadimJsonParser = sadimJsonParser;
    }

    //    @KafkaListener(containerFactory = "kafkaListenerSadim",
//            topicPartitions = {@TopicPartition(topic = "PA-MU.NLMK.P3.HSM",
//            partitionOffsets = @PartitionOffset(partition = "0", initialOffset = "0")
//            ),
//            })
    @KafkaListener(containerFactory = "kafkaListenerSadim",topics = {"${kafka.sadim.topic}"})
    public void receiveMessageReq(@Payload ConsumerRecord consumerRecord) {

        log.info("SADIM offset: {}", consumerRecord.offset());

        val attestationParam = sadimJsonParser.getParam(consumerRecord.value().toString());

        log.info("--- SADIM message with offset: {}; attestationParam:{}",consumerRecord.offset(),attestationParam.get());
    }
}
