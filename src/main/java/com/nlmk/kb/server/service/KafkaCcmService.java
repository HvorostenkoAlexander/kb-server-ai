package com.nlmk.kb.server.service;

import com.nlmk.kb.server.exception.DateTimeParseException;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import nlmk.l3.ccm.pgp.AttestationRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaCcmService {

    @Value("${kafka.ack.nack.sleep-time}")
    private long sleepTime;

    private final PamClientService pamClientService;
    private final CommonConverter converter;
    private final CcmMessageService messageService;
    private final CcmMessageConverter messageConverter;

    @KafkaListener(containerFactory = "kafkaListenerContainerFactoryReq",
            topicPartitions = {@TopicPartition(topic = "${kafka.ccm.topicReq}",
                    partitionOffsets =
                    @PartitionOffset(partition = "0", initialOffset = "0")),})
//    @KafkaListener(containerFactory = "kafkaListenerContainerFactoryReq",
//            topics = {"${kafka.ccm.topicReq}"}
//    )
    @Timed(value = "kafka_listener", percentiles = {0.99, 0.95})
    public void receiveMessageReq(@Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                  @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                                  @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                                  @Header(KafkaHeaders.OFFSET) int offset,
                                  @Header(KafkaHeaders.RECEIVED_TIMESTAMP) String timestamp,
                                  @Payload AttestationRequest request,
                                  Acknowledgment ack) {

        log.info("--- receiveMessageReq from CCM AttestationRequest:" +
                        "partition: {}; " +
                        "offset: {}; " +
                        "key: {}; " +
                        "timestamp: {}; " +
                        "request.ts:{}; " +
                        "request.op: {}; " +
                        "request.pk.id: {}; " +
                        "request.data.primeId: {} ",
                partition, offset, key, timestamp,
                request.getTs(),
                request.getOp(),
                request.getPk().getId(),
                request.getData().getPrimeId());

        try { // todo сохранять сведения об ошибках в отдельной сущности
            val requestMessage = messageConverter.fromCcmAttestationRequest(
                    request,
                    topic,
                    key,
                    partition,
                    offset,
                    timestamp
            );

            val savedRequest = messageService.save(requestMessage).orElseThrow(
                    () -> new RuntimeException("Не удалось сохранить сообщение partition: " + partition
                            + "; offset: " + offset)
            );

            val pamResult = pamClientService.postAttestationRequest(savedRequest.getRequest());

            if (pamResult != null) {
                savedRequest.setStatus("recived");
                savedRequest.setKafkaTs(new Date());
            }

            ack.acknowledge();
        } catch (DateTimeParseException ddpe) {
            ack.acknowledge();
            throw new DateTimeParseException("переброс: " + ddpe);
        } catch (Exception e) {
            ack.nack(sleepTime);
            throw new RuntimeException("переброс: " + e);
        }
    }
}
