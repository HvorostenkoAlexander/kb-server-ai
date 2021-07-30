package com.nlmk.kb.server.service;

import com.nlmk.kb.server.exception.DateTimeParseException;
import com.nlmk.kb.server.service.impl.PdmMessageHandlerImpl;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaPdmService {

    @Value("${kafka.ack.nack.sleep-time}")
    private long sleepTime;

    private final PdmMessageHandler pdmMessageHandler;

    @KafkaListener(containerFactory = "kafkaListenerContainerFactoryPdm",
            topics = {
                    "${kafka.pdm.topic.microstructure}",
                    "${kafka.pdm.topic.asap-chemical-properties}",
                    "${kafka.pdm.topic.equivalents}",
                    "${kafka.pdm.topic.match-tk-num}",
                    "${kafka.pdm.topic.match-rabplan-num}",
                    "${kafka.pdm.topic.pcm}",
                    "${kafka.pdm.topic.asap-tol-links}",
                    "${kafka.pdm.topic.tol-thick}",
                    "${kafka.pdm.topic.kat-steel-4041}",
                    "${kafka.pdm.topic.tol-width}",
                    "${kafka.pdm.topic.tol-length}",
                    "${kafka.pdm.topic.asap-mech-properties}",
                    "${kafka.pdm.topic.tol-evenness}",
                    "${kafka.pdm.topic.tk-num}",
                    "${kafka.pdm.topic.ceq}",
                    "${kafka.pdm.topic.mech-properties}",
                    "${kafka.pdm.topic.chemical-properties}"
            }
    )
    @Timed(value = "kafka_listener", percentiles = {0.99, 0.95})
    public void receiveMessageReq(@Payload ConsumerRecord request, Acknowledgment ack) {

        log.info("PDM consumer record: topic: {}; partition: {}; offset: {}, key: {}",
                request.topic(),
                request.partition(),
                request.offset(),
                request.key()
        );

        try {
            if (pdmMessageHandler.handleConsumerRecord(request)) {
                ack.acknowledge();
            } else {
                ack.nack(sleepTime);
            }
        } catch (DateTimeParseException ddpe) {
            ack.acknowledge();
            throw new DateTimeParseException("переброс: " + ddpe);
        } catch (Exception e) {
            ack.nack(sleepTime);
            throw new RuntimeException("переброс: " + e);
        }
    }
}
