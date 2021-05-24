package com.nlmk.kb.server.service;

import com.nlmk.kb.server.entity.PreAttestationParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final PreAttestationParamService paramService;

    public KafkaSadimService(@Qualifier("sadimStreamApiParser")
                                     SadimJsonParser sadimJsonParser,
                             PreAttestationParamService paramService) {
        this.sadimJsonParser = sadimJsonParser;
        this.paramService = paramService;
    }

    //    @KafkaListener(containerFactory = "kafkaListenerSadim",
//            topicPartitions = {@TopicPartition(topic = "PA-MU.NLMK.P3.HSM",
//            partitionOffsets = @PartitionOffset(partition = "0", initialOffset = "0")
//            ),
//            })
    @KafkaListener(containerFactory = "kafkaListenerSadim",topics = {"${kafka.sadim.topic}"})
    public void receiveMessageReq(@Payload ConsumerRecord consumerRecord) {

        log.info("SADIM offset: {}", consumerRecord.offset());

        Optional<PreAttestationParam> attestationParam = sadimJsonParser.getParam(consumerRecord.value().toString());
        log.info("--- SADIM message with offset: {}; attestationParam:{}",consumerRecord.offset(),attestationParam.get());

        if (attestationParam.isPresent()){
            attestationParam = paramService.save(attestationParam.get());
            log.info("--- SADIM param successfully saved. param.primeId:{} ",attestationParam.get().getPrimeId());
        }
    }
}
