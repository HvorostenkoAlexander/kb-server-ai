package com.nlmk.kb.server.service;

import com.nlmk.kb.server.config.KbConstants;
import com.nlmk.kb.server.exception.DateTimeParseException;
import com.nlmk.kb.server.exception.KafkaMessageProcessingException;
import com.nlmk.kb.server.service.pdm.PdmMessageHandler;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@Slf4j
@Service
public class PdmKafkaService {

    private final long sleepTime;
    private final PdmMessageHandler pdmMessageHandler;

    public PdmKafkaService(@Value("${kafka.ack.nack.sleep-time}") long sleepTime,
                           PdmMessageHandler pdmMessageHandler) {
        this.sleepTime = sleepTime;
        this.pdmMessageHandler = pdmMessageHandler;
    }

    @KafkaListener(
            containerFactory = "pdmKafkaListenerContainerFactory",
            topics = {
                    "${kafka.pdm.topic.microstructure}",
                    "${kafka.pdm.topic.asap-chemical-properties}",
                    "${kafka.pdm.topic.equivalents}",
                    "${kafka.pdm.topic.match-tk-num}",
                    "${kafka.pdm.topic.match-rabplan-num}",
                    "${kafka.pdm.topic.asap-tol-links}",
                    "${kafka.pdm.topic.tol-thick}",
                    "${kafka.pdm.topic.kat-steel-4041}",
                    "${kafka.pdm.topic.tol-width}",
                    "${kafka.pdm.topic.tol-length}",
                    "${kafka.pdm.topic.asap-mech-properties}",
                    "${kafka.pdm.topic.tol-evenness}",
                    "${kafka.pdm.topic.tk-num}",
                    "${kafka.pdm.topic.mech-properties}",
                    "${kafka.pdm.topic.chemical-properties}",
                    "${kafka.pdm.topic.asap-mech-properties-dt}",
                    "${kafka.pdm.topic.phys-mech-prop-anis-steel}",
                    "${kafka.pdm.topic.tol-evenness-dt}",
                    "${kafka.pdm.topic.tol-thick-dt}",
                    "${kafka.pdm.topic.tol-width-dt}",
                    "${kafka.pdm.topic.sp-chemical-properties-notes}",
                    "${kafka.pdm.topic.tol-shape-slab}",
                    "${kafka.pdm.topic.register-equivalents}",
                    "${kafka.pdm.topic.min-number-samp-chem}",
                    "${kafka.pdm.topic.scheme-stripping-slab}",
                    "${kafka.pdm.topic.macrostructure}"
            }
    )
    @Timed(value = "kafka_listener", percentiles = {0.99, 0.95})
    public void receiveMessageReq(@Payload ConsumerRecord<Object, Object> consumerRecord,
                                  Acknowledgment ack) {
        log.info("receiveMessageReq (PDM): topic [{}], partition [{}], offset [{}], key [{}]",
                consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset(), consumerRecord.key());

        try {
            if (pdmMessageHandler.handleConsumerRecord(consumerRecord)) {
                ack.acknowledge();
            } else {
                ack.nack(sleepTime);
            }
        } catch (DateTimeParseException e) {
            log.warn("receiveMessageReq, DateTimeParseException", e);
            ack.acknowledge();
            throw new DateTimeParseException(MessageFormat.format(KbConstants.THROW_EXC_MESSAGE_TEMPLATE, e));
        } catch (Exception e) {
            log.warn("receiveMessageReq, Exception", e);
            ack.nack(sleepTime);
            throw new KafkaMessageProcessingException(MessageFormat.format(KbConstants.THROW_EXC_MESSAGE_TEMPLATE, e));
        }
    }

}
