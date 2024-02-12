package com.nlmk.kb.server.service.listener;

import com.nlmk.kb.server.exception.DateTimeParseException;
import com.nlmk.kb.server.exception.RemoteServiceSenderException;
import com.nlmk.kb.server.exception.SadimJsonProcessingException;
import com.nlmk.kb.server.exception.KafkaMessageProcessingException;
import com.nlmk.kb.server.service.ccm.CcmCommonService;
import com.nlmk.kb.server.service.sadim.SadimMessageService;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

import static com.nlmk.kb.server.config.KbConstants.LISTENER_EXC_MESSAGE_TEMPLATE;

@Slf4j
@Service
public class SadimKafkaService {

    private final long sleepTime;
    private final SadimMessageService messageService;
    private final CcmCommonService ccmCommonService;

    public SadimKafkaService(@Value("${kafka.ack.nack.sleep-time}") long sleepTime,
                             SadimMessageService messageService,
                             CcmCommonService ccmCommonService) {
        this.sleepTime = sleepTime;
        this.messageService = messageService;
        this.ccmCommonService = ccmCommonService;
    }

    @KafkaListener(containerFactory = "sadimKafkaListenerContainerFactory",
            topics = {"${kafka.sadim.topic}"}
    )
    @Timed(value = "kafka_listener", percentiles = {0.99, 0.95})
    public void receiveMessageReq(@Payload ConsumerRecord<Object, Object> consumerRecord,
                                  Acknowledgment ack) {

        log.info("receiveMessageReq (SADIM): topic [{}], partition [{}], offset [{}], key [{}]",
                consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset(), consumerRecord.key());
        String primeId;

        try {
            primeId = messageService.saveMessage(consumerRecord);
            ack.acknowledge();
        } catch (SadimJsonProcessingException e) {
            log.warn("receiveMessageReq, SadimJsonProcessingException", e);
            ack.acknowledge();
            throw new SadimJsonProcessingException(MessageFormat.format(LISTENER_EXC_MESSAGE_TEMPLATE, e));
        } catch (DateTimeParseException e) {
            log.warn("receiveMessageReq, DateTimeParseException", e);
            ack.acknowledge();
            throw new DateTimeParseException(MessageFormat.format(LISTENER_EXC_MESSAGE_TEMPLATE, e));
        } catch (RemoteServiceSenderException e) {
            log.warn("receiveMessageReq, RemoteServiceSenderException", e);
            ack.nack(sleepTime);
            throw new RemoteServiceSenderException(MessageFormat.format(LISTENER_EXC_MESSAGE_TEMPLATE, e));
        } catch (Exception e) {
            log.warn("receiveMessageReq, Exception", e);
            ack.nack(sleepTime);
            throw new KafkaMessageProcessingException(MessageFormat.format(LISTENER_EXC_MESSAGE_TEMPLATE, e));
        }

        // нужна очередь ошибочных сообщений (dead letter queue, DLQ) и отдельный обработчик, чтобы не тормозить основную очередь.

        sadimRePostAttestation(primeId);
    }

    /**
     * Повторный запрос на Аттестацию
     *
     * @param primeId идентификатор
     */
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
