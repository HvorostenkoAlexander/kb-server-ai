package com.nlmk.kb.server.service.pdm;

import com.nlmk.kb.server.entity.pdm.PdmMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdmMessageHandlerImpl implements PdmMessageHandler {

    private final DictionaryConfigService dictionaryService;
    private final PdmMessageConverter messageConverter;
    private final PdmMessageService messageService;

    @Override
    public boolean handleConsumerRecord(final ConsumerRecord<Object, Object> consumerRecord) {
        log.debug("handleConsumerRecord: [{}]", consumerRecord);

        if (isTopicDisabled(consumerRecord.topic())) {
            log.warn("handleConsumerRecord: topic: [{}] is DISABLED", consumerRecord.topic());
            return false;
        }

        PdmMessage message = messageConverter.fromConsumerRecord(consumerRecord);
        Optional<PdmMessage> savedMessage = messageService.save(message);

        if (savedMessage.isPresent()) {
            messageService.sendToNsi(savedMessage.get());
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

}
