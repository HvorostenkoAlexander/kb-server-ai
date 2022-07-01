package com.nlmk.kb.server.service;

import com.nlmk.kb.server.exception.CcmPtsKafkaException;
import com.nlmk.kb.server.exception.DateTimeParseException;
import com.nlmk.kb.server.service.ccm.CcmCommonService;
import com.nlmk.kb.server.service.ccm.CcmMessageAdapter;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import nlmk.l3.ccm.pts.EnumOp;
import nlmk.l3.ccm.pts.AttestationRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CcmPtsKafkaService {

    private final long sleepTime;
    private final CcmCommonService ccmCommonService;
    private final CcmMessageAdapter<AttestationRequest> ccmMessageAdapter;

    public CcmPtsKafkaService(@Value("${kafka.ack.nack.sleep-time}") long sleepTime,
                              CcmCommonService ccmCommonService,
                              CcmMessageAdapter<AttestationRequest> ccmMessageAdapter) {
        this.sleepTime = sleepTime;
        this.ccmCommonService = ccmCommonService;
        this.ccmMessageAdapter = ccmMessageAdapter;
    }

    @KafkaListener(containerFactory = "ccmPtsKafkaListenerContainerFactory",
            topics = {"${kafka.ccm.pts.topicReq}"}
    )
    @Timed(value = "kafka_listener", percentiles = {0.99, 0.95})
    public void receiveMessageReq(@Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                  @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                                  @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                                  @Header(KafkaHeaders.OFFSET) int offset,
                                  @Header(KafkaHeaders.RECEIVED_TIMESTAMP) String timestamp,
                                  @Payload AttestationRequest request,
                                  Acknowledgment ack) {

        log.info("CCM PTS AttestationRequest: partition: {}; offset: {}; key: {}; timestamp: {}; request.ts:{}; request.op: {}; request.pk.id: {}; ", partition, offset, key, timestamp, request.getTs(), request.getOp(), request.getPk().getId());

        try {
            final var requestMessage = ccmMessageAdapter.adapt(request, topic, key, partition, offset);

            if (request.getOp() == EnumOp.D
                    || requestMessage.getRequest().getValue() == null
                    || requestMessage.getRequest().getValue().getData() == null) {
                log.warn("receiveMessageReq, SKIP send attestation request, partition {}, offset {}, key {}: wrong Op and Data",
                        partition, offset, key);
            } else {
                // отправка запроса при наличии тела и правильной операции
                ccmCommonService.postAttestation(requestMessage);
            }
            ack.acknowledge();
        } catch (DateTimeParseException e) {
            log.warn("receiveMessageReq, DateTimeParseException", e);
            ack.acknowledge();
            throw new DateTimeParseException("переброс: " + e);
        } catch (Exception e) {
            log.warn("receiveMessageReq, Exception", e);
            ack.nack(sleepTime);
            throw new CcmPtsKafkaException("переброс: " + e);
        }
    }

}
