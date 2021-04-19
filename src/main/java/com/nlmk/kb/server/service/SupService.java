package com.nlmk.kb.server.service;

import com.nlmk.kb.server.entity.IntegralParam;
import com.nlmk.kb.server.util.ParamConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nlmk.l3.sup.IntegralParameters;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
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
    public void receiveMessage(@Payload IntegralParameters supIntegralParameters) {
        log.info("--- received integralParameters: {}", supIntegralParameters);

        IntegralParam ip = ParamConverter.toIntegralParam(supIntegralParameters);
      //  integralParamService.processingIntegralParam(ip);
    }
}
