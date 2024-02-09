package com.nlmk.kb.server.service.listener;

import com.nlmk.kb.server.exception.DateTimeParseException;
import com.nlmk.kb.server.exception.KafkaMessageProcessingException;
import com.nlmk.kb.server.service.sap.SapMessageHandler;
import com.nlmk.s3.proxy.s3notification;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

import static com.nlmk.kb.server.config.KbConstants.THROW_EXC_MESSAGE_TEMPLATE;

@Slf4j
@Service
public class SapZordersKafkaService {

    private final long sleepTime;
    private final SapMessageHandler sapMessageHandler;

    public SapZordersKafkaService(@Value("${kafka.ack.nack.sleep-time}") long sleepTime,
                                  SapMessageHandler sapMessageHandler) {
        this.sleepTime = sleepTime;
        this.sapMessageHandler = sapMessageHandler;
    }

    @KafkaListener(containerFactory = "sapKafkaListenerContainerFactory",
            topics = {"${kafka.sap.topic.s3.idoczordrs}"}
    )
    @Timed(value = "kafka_listener", percentiles = {0.99, 0.95})
    public void receiveMessageReq(@Payload ConsumerRecord<String, s3notification> consumerRecord,
                                         Acknowledgment ack) {
        log.info("receiveZordersMessageReq (SAP): topic [{}], partition [{}], offset [{}], key [{}]",
                consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset(), consumerRecord.key());

        try {
            if (sapMessageHandler.handleConsumerRecord(consumerRecord)) {
                ack.acknowledge();
                log.debug("receiveZordersMessageReq (SAP): Sending ack for topic [{}], partition [{}], offset [{}], key [{}]",
                        consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset(), consumerRecord.key());
            } else {
                log.debug("receiveZordersMessageReq (SAP): Sending nack for topic [{}], partition [{}], offset [{}], key [{}]",
                        consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset(), consumerRecord.key());
                ack.nack(sleepTime);
            }
        } catch (DateTimeParseException e) {
            log.warn("receiveZordersMessageReq, DateTimeParseException", e);
            ack.acknowledge();
            throw new DateTimeParseException(MessageFormat.format(THROW_EXC_MESSAGE_TEMPLATE, e));
        } catch (Exception e) {
            log.warn("receiveZordersMessageReq, Exception", e);
            ack.nack(sleepTime);
            throw new KafkaMessageProcessingException(MessageFormat.format(THROW_EXC_MESSAGE_TEMPLATE, e));
        }
    }

}
