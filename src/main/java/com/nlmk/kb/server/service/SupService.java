package com.nlmk.kb.server.service;

import com.nlmk.kb.server.entity.IntegralParam;
import com.nlmk.kb.server.entity.KafkaIntegralParamMessage;
import com.nlmk.kb.server.util.ParamConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nlmk.l3.sup.IntegralParameters;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupService {

    private final KafkaIntegralParamMessageService integralParamMessageService;

    // внимание, при работе с продуктовым топиком, количество партиций будет > 1
    // сейчас при таких настройках сведения только из одной партиции (как в тестовом топике)
    //todo убрать лишнее из KafkaListener

    @KafkaListener(containerFactory = "kafkaListenerContainerFactoryIp",
            topicPartitions = {@TopicPartition(topic = "${kafka.sup.topicIp}",
                    partitionOffsets =
                    @PartitionOffset(partition = "0", initialOffset = "0")),})
    public void receiveMessage(@Headers MessageHeaders headers,
                               @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                               @Header(KafkaHeaders.OFFSET) int offset,
                               @Header(KafkaHeaders.RECEIVED_TIMESTAMP) String timestamp,
                               @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                               @Payload IntegralParameters supIntegralParameters) {

        log.info("--- received message: Key: {} ; Timestamp: {}; offset: {}; topic: {}; value:{}",
                key, timestamp, offset, topic,
                supIntegralParameters);

        IntegralParam ip = ParamConverter.toIntegralParam(supIntegralParameters);

        KafkaIntegralParamMessage receivedMessage = KafkaIntegralParamMessage.builder()
                .key(key)
                .timestamp(timestamp)
                .offset(offset)
                .topic(topic)
                .param(ip)
                .build();

        integralParamMessageService.messageProcessing(receivedMessage);
    }
}
