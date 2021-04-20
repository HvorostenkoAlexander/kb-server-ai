package com.nlmk.kb.server.service;

import com.nlmk.kb.server.entity.IntegralParam;
import com.nlmk.kb.server.util.ParamConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nlmk.l3.sup.IntegralParameters;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupService {

    private final IntegralParamService integralParamService;

    //todo убрать лишнее из KafkaListener

    @KafkaListener(containerFactory = "kafkaListenerContainerFactoryIp",
            topicPartitions = {@TopicPartition(topic = "${kafka.sup.topicIp}",
                    partitionOffsets =
                    @PartitionOffset(partition = "0", initialOffset = "0")),})
    public void receiveMessage(@Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                               @Header(KafkaHeaders.TIMESTAMP) String timestamp,
                               @Header(KafkaHeaders.OFFSET) String offset,
                               @Payload IntegralParameters supIntegralParameters) {
        log.info("--- received message: Key: {} ; Timestamp: {}; offset: {}; value:{}",
                key, timestamp, offset,
                supIntegralParameters);

        IntegralParam ip = ParamConverter.toIntegralParam(supIntegralParameters);

        //integralParamService.save(ip);
    }
}
