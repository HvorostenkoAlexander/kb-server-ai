package com.nlmk.kb.server.service.listener;

import com.nlmk.kb.server.config.KbConstants;
import com.nlmk.kb.server.exception.KafkaMessageProcessingException;
import com.nlmk.kb.server.exception.ZifraMessageParserException;
import com.nlmk.kb.server.service.zifra.ZifraMessageHandler;
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
public class ZifraKafkaService {

    private final long sleepTime;
    private final ZifraMessageHandler zifraMessageHandler;

    public ZifraKafkaService(@Value("${kafka.ack.nack.sleep-time}") long sleepTime,
                             ZifraMessageHandler zifraMessageHandler) {
        this.sleepTime = sleepTime;
        this.zifraMessageHandler = zifraMessageHandler;
    }

    @KafkaListener(
            containerFactory = "zifraKafkaListenerContainerFactory",
            topics = {
                    "${kafka.zifra.topic.sp-customer}",
                    "${kafka.zifra.topic.sp-customer-group}",
                    "${kafka.zifra.topic.sp-group-and-customer}",
                    "${kafka.zifra.topic.sp-om-data-type}",
                    "${kafka.zifra.topic.sp-attributes}",
                    "${kafka.zifra.topic.sp-l2code-place}",
                    "${kafka.zifra.topic.sp-attribute-attestation-group}",
                    "${kafka.zifra.topic.sp-measure}",
                    "${kafka.zifra.topic.sp-data-type}",
                    "${kafka.zifra.topic.sp-analysis-type}",
                    "${kafka.zifra.topic.sp-certification-step-type}",
                    "${kafka.zifra.topic.sp-sampling-topology}",
                    "${kafka.zifra.topic.sp-dimension}",
                    "${kafka.zifra.topic.sp-place-type}"
            }
    )
    @Timed(value = "kafka_listener", percentiles = {0.99, 0.95})
    public void receiveMessageReq(@Payload ConsumerRecord<Object, Object> consumerRecord, Acknowledgment ack) {
        log.info("receiveMessageReq (ZIFRA): topic [{}], partition [{}], offset [{}], key [{}]",
                consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset(), consumerRecord.key());

        try {
            if (zifraMessageHandler.handleConsumerRecord(consumerRecord)) {
                log.debug("receiveMessageReq (ZIFRA): Sending ack for topic [{}], partition [{}], offset [{}], key [{}]",
                        consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset(), consumerRecord.key());
                ack.acknowledge();
            } else {
                log.debug("receiveMessageReq (ZIFRA): Sending nack for topic [{}], partition [{}], offset [{}], key [{}]",
                        consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset(), consumerRecord.key());
                ack.acknowledge();
            }
        } catch (ZifraMessageParserException e) {
            log.warn("receiveMessageReq, Exception", e);
            ack.acknowledge();
            throw new KafkaMessageProcessingException(MessageFormat.format(KbConstants.LISTENER_EXC_MESSAGE_TEMPLATE, e));
        } catch (Exception e) {
            log.warn("receiveMessageReq, Exception", e);
            ack.nack(sleepTime);
            throw new KafkaMessageProcessingException(MessageFormat.format(KbConstants.LISTENER_EXC_MESSAGE_TEMPLATE, e));
        }
    }

}
