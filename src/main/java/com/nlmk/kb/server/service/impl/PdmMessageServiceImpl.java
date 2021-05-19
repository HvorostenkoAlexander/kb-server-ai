package com.nlmk.kb.server.service.impl;

import com.nlmk.kb.server.entity.pdm.PdmMessage;
import com.nlmk.kb.server.repository.PdmMessageRepository;
import com.nlmk.kb.server.service.PdmMessageService;
import com.nlmk.kb.server.util.PdmConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdmMessageServiceImpl implements PdmMessageService {

    private final PdmMessageRepository messageRepository;

    @Override
    public Optional<PdmMessage> save(ConsumerRecord record) {
        if (record == null) {
            log.warn("ConsumerRecord is NULL");
            return Optional.empty();
        }

        if (messageRepository.existsByTopicAndOffsetAndPartition(
                record.topic(), record.offset(), record.partition())) {
            log.debug("--- the message with offset: {} from topic: {} is already present in the database. message key: {} ",
                    record.offset(), record.topic(), record.key());

            // return Optional.empty();//todo закомментированно с цель проверки работы алгоритмов передачи в nsi-server, как будет проверено ВЕРНУТЬ!
            return Optional.of(messageRepository.findByTopicAndOffsetAndPartition(record.topic(), record.offset(), record.partition()));
        }

        val pdmMessege = PdmConverter.fromConsumerRecord(record);

        messageRepository.save(pdmMessege);

        log.debug("--- PDM Successfully saved message: offset: {}; topic: {}, key:{};",
                record.offset(),
                record.topic(),
                record.key());

        return Optional.of(pdmMessege);
    }

    @Override
    public Optional<PdmMessage> save(PdmMessage message) {
        return Optional.of(messageRepository.save(message));
    }
}
