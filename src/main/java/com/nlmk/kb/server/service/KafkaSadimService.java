package com.nlmk.kb.server.service;

import com.nlmk.kb.server.exception.DateTimeParseException;
import com.nlmk.kb.server.exception.SadimJsonProcessingException;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaSadimService {

    @Value("${kafka.ack.nack.sleep-time}")
    private long sleepTime;
    private final SadimMessageService messageService;

    @KafkaListener(containerFactory = "kafkaListenerSadim", topics = {"${kafka.sadim.topic}"})
    @Timed(value = "kafka_listener", percentiles = {0.99, 0.95})
    public void receiveMessageReq(@Payload ConsumerRecord consumerRecord,
                                  Acknowledgment ack) {

        log.debug("SADIM message with partition: [{}]; offset: [{}];", consumerRecord.partition(), consumerRecord.offset());

        try {
            final var sadimMessage = messageService.saveMessage(consumerRecord);
            log.info("saved SADIM massage. Partition: [{}]; offset: [{}]; param: [{}];",
                    sadimMessage.getPartition(), sadimMessage.getOffset(), sadimMessage.getParam());

            ack.acknowledge();

        } catch (SadimJsonProcessingException sjpe) {
            ack.acknowledge();
            throw new SadimJsonProcessingException("переброс: " + sjpe);
        } catch (DateTimeParseException ddpe) {
            ack.acknowledge();
            throw new DateTimeParseException("переброс: " + ddpe);
        } catch (Exception e) {
            ack.nack(sleepTime);
            throw new RuntimeException("переброс: " + e);
        }
    }
}
