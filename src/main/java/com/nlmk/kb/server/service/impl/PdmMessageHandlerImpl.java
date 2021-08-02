package com.nlmk.kb.server.service.impl;

import com.nlmk.kb.server.entity.pdm.PdmMessage;
import com.nlmk.kb.server.service.DictionaryConfigService;
import com.nlmk.kb.server.service.NsiClientService;
import com.nlmk.kb.server.service.PdmMessageConverter;
import com.nlmk.kb.server.service.PdmMessageHandler;
import com.nlmk.kb.server.service.PdmMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdmMessageHandlerImpl implements PdmMessageHandler {

    private final DictionaryConfigService dictionaryService;
    private final PdmMessageConverter messageConverter;
    private final PdmMessageService messageService;
    private final NsiClientService nsiClientService;

    @Override
    public boolean handleConsumerRecord(final ConsumerRecord record) {
        log.info("handleConsumerRecord: [{}]", record);

        if (isTopicDisabled(record.topic())) {
            log.warn("handleConsumerRecord: topic: [{}] is DISABLED", record.topic());
            return false;
        }

        PdmMessage message = messageConverter.fromConsumerRecord(record);

        Optional<PdmMessage> savedMessage = messageService.save(message);

        if (savedMessage.isPresent()) {
            message = savedMessage.get();

            ResponseEntity<Long> response = nsiClientService.sendPdmDictionary(message);
            setStatusMessage(message, response.getStatusCode());

            messageService.update(message);
            return true;
        } else {
            throw new RuntimeException(
                    String.format("Не удалось сохранить PdmMessage: [%s]", message)
            );
        }
    }

    private boolean isTopicDisabled(String topic) {
        return !dictionaryService.findByTopic(topic).getEnabled();
    }

    private void setStatusMessage(PdmMessage message, HttpStatus status) {
        if (status == HttpStatus.ACCEPTED ||
                status == HttpStatus.NOT_FOUND ||
                status == HttpStatus.OK) {
            message.setPosted(true);
            message.setKbReceiptTs(new Date());
            message.setNote(status.toString());
        } else {
            message.setPosted(false);
            message.setKbReceiptTs(new Date());
            message.setNote(status.toString());
        }
    }
}
