package com.nlmk.kb.server.service;

import com.nlmk.kb.server.entity.IntegralParam;
import com.nlmk.kb.server.entity.KafkaIntegralParamMessage;
import com.nlmk.kb.server.entity.KafkaUnrecoverableParamMessage;
import com.nlmk.kb.server.entity.UnrecoverableParam;
import com.nlmk.kb.server.util.ParamConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nlmk.l3.sup.IntegralParameters;
import nlmk.l3.sup.UnrecoverableParametersTrends;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupService {

    private final KafkaIntegralParamMessageService integralParamMessageService;
    private final KafkaUnrecoverableParamMessageService unrecoverableParamMessageService;
    private final IntegralParamService integralParamService; // todo убрать, используется только с целью проверки

    // внимание, при работе с продуктовым топиком, количество партиций будет > 1
    // сейчас при таких настройках сведения только из одной партиции (как в тестовом топике)
    //todo убрать лишнее из KafkaListener

    @KafkaListener(containerFactory = "kafkaListenerContainerFactoryIp",
            topicPartitions = {@TopicPartition(topic = "${kafka.sup.topicIp}",
                    partitionOffsets =
                    @PartitionOffset(partition = "0", initialOffset = "0")),})
    public void receiveMessageIp(@Headers MessageHeaders headers,
                               @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                                 @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                               @Header(KafkaHeaders.OFFSET) int offset,
                               @Header(KafkaHeaders.RECEIVED_TIMESTAMP) String timestamp,
                               @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                               @Payload IntegralParameters supIntegralParameters) {

        log.info("--- received message: Key: {} ; Timestamp: {};partition {}; offset: {}; topic: {}; value:{}",
                key, timestamp, partition, offset, topic,
                supIntegralParameters);

        IntegralParam ip = ParamConverter.toIntegralParam(supIntegralParameters);

        KafkaIntegralParamMessage receivedMessage = KafkaIntegralParamMessage.builder()
                .key(key)
                .timestamp(timestamp)
                .partition(partition)
                .offset(offset)
                .topic(topic)
                .param(ip)
                .build();

        integralParamMessageService.messageProcessing(receivedMessage);

        List<IntegralParam> integralParams = integralParamService.findByRecordPk(ip.getRecordPk());
        log.info("--- integralParams with recordPK: {} count:{}; values:{} ",ip.getRecordPk(),integralParams.size(),integralParams);
    }

    @KafkaListener(containerFactory = "kafkaListenerContainerFactoryUp",
            topicPartitions = {@TopicPartition(topic = "${kafka.sup.topicUp}",
                    partitionOffsets =
                    @PartitionOffset(partition = "0", initialOffset = "0")),})
    public void receiveMessageUp(@Headers MessageHeaders headers,
                                 @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                                 @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                                 @Header(KafkaHeaders.OFFSET) int offset,
                                 @Header(KafkaHeaders.RECEIVED_TIMESTAMP) String timestamp,
                                 @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                 @Payload UnrecoverableParametersTrends supUnrecoverableParametersTrends) {

        log.info("--- received message: Key: {} ; Timestamp: {}; partition {}; offset: {}; topic: {}; value:{}",
                key, timestamp, partition, offset, topic,
                supUnrecoverableParametersTrends);

        UnrecoverableParam up = ParamConverter.toUnrecoverableParam(supUnrecoverableParametersTrends);

        KafkaUnrecoverableParamMessage receivedMessage = KafkaUnrecoverableParamMessage.builder()
                .key(key)
                .timestamp(timestamp)
                .partition(partition)
                .offset(offset)
                .topic(topic)
                .param(up)
                .build();

        unrecoverableParamMessageService.messageProcessing(receivedMessage);
    }
}
