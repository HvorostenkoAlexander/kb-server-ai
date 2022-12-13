package com.nlmk.kb.server.service.pdm;

import com.nlmk.kb.server.entity.pdm.PdmMessage;
import com.nlmk.kb.server.exception.PdmMessageHandlerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
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

        final var dictConf = dictionaryService.findByTopic(consumerRecord.topic());

        if (Boolean.FALSE.equals(dictConf.getEnabled())) {
            log.warn("handleConsumerRecord: тема [{}] отключена", consumerRecord.topic());
            return false;
        }

        PdmMessage message = messageConverter.fromConsumerRecord(consumerRecord);
        Optional<PdmMessage> savedMessage = messageService.save(message);

        if (savedMessage.isPresent()) {
            messageService.sendToNsi(savedMessage.get());
            return true;
        } else {
            throw new PdmMessageHandlerException(
                    MessageFormat.format("Не удалось сохранить PdmMessage: [{0}]", message)
            );
        }
    }

}
