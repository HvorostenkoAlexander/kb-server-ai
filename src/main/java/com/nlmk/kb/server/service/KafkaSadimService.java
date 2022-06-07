package com.nlmk.kb.server.service;

import com.nlmk.kb.server.exception.DateTimeParseException;
import com.nlmk.kb.server.exception.SadimJsonProcessingException;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaSadimService {

    private static final String EXC_MESS = "переброс: %s";
    private final long sleepTime;
    private final SadimMessageService messageService;
    private final CcmCommonService ccmCommonService;

    public KafkaSadimService(@Value("${kafka.ack.nack.sleep-time}") long sleepTime,
                             SadimMessageService messageService,
                             CcmCommonService ccmCommonService) {
        this.sleepTime = sleepTime;
        this.messageService = messageService;
        this.ccmCommonService = ccmCommonService;
    }

    @KafkaListener(containerFactory = "kafkaListenerSadim", topics = {"${kafka.sadim.topic}"})
    @Timed(value = "kafka_listener", percentiles = {0.99, 0.95})
    public void receiveMessageReq(@Payload ConsumerRecord<Object, Object> consumerRecord,
                                  Acknowledgment ack) {

        log.debug("SADIM message with partition: [{}]; offset: [{}];", consumerRecord.partition(), consumerRecord.offset());
        String primeId;

        try {
            primeId = messageService.saveMessage(consumerRecord);

            ack.acknowledge();
        } catch (SadimJsonProcessingException sjpe) {
            ack.acknowledge();
            throw new SadimJsonProcessingException(String.format(EXC_MESS, sjpe));
        } catch (DateTimeParseException ddpe) {
            ack.acknowledge();
            throw new DateTimeParseException(String.format(EXC_MESS, ddpe));
        } catch (Exception e) {
            ack.nack(sleepTime);
            throw new RuntimeException(String.format(EXC_MESS, e));
        }

        sadimRePostAttestation(primeId);
    }

    private void sadimRePostAttestation(String primeId) {
        try {
            ccmCommonService.rePostAttestation(primeId);
        } catch (Exception ex) {
            log.debug("Повторная отправка запроса на аттестацию primeId:[{}] закончилась неудачей: [{}]",
                    primeId,
                    ex.getMessage());
        }
    }

}
