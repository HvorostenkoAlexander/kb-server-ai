package com.nlmk.kb.server.service;

import com.nlmk.kb.server.entity.pam.Value;
import com.nlmk.kb.server.util.ValueConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import nlmk.l3.ccm.pgp.AttestationRequest;
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
public class KafkaCcmService {

    private final PamClientService pamClientService;

    @KafkaListener(containerFactory = "kafkaListenerContainerFactoryReq",
            topicPartitions = {@TopicPartition(topic = "${kafka.ccm.topicReq}",
                    partitionOffsets =
                    @PartitionOffset(partition = "0", initialOffset = "0")),})
//    @KafkaListener(containerFactory = "kafkaListenerContainerFactoryReq",
//            topics = {"${kafka.ccm.topicReq}"}
//    )
    public void receiveMessageReq(@Header(KafkaHeaders.OFFSET) int offset,
                                  @Payload AttestationRequest request) {

        log.debug("--- receiveMessageReq from CCM AttestationRequest: ts: {};" +
                        " op: {}; pk.id: {}; data.primeId: {}",
                request.getTs(), request.getOp(), request.getPk().getId(), request.getData().getPrimeId());

        if (offset==104) {
            log.info("--- !!! request with offset = 104: {}",request);
        }

        val value = ValueConverter.fromKafkaAttestationRequest(request);

        //todo убрать отправку данных в pam в другое место. тут должна быть только обработка сообщения.
        pamClientService.postAttestationRequest(value);
    }
}
