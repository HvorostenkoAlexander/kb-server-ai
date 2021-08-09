package com.nlmk.kb.server.service.impl;

import com.nlmk.kb.server.entity.SadimMessage;
import com.nlmk.kb.server.exception.SadimJsonProcessingException;
import com.nlmk.kb.server.repository.SadimMessageRepository;
import com.nlmk.kb.server.service.SadimJsonParser;
import com.nlmk.kb.server.service.SadimMessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class SadimMessageServiceImpl implements SadimMessageService {

    private final SadimJsonParser sadimJsonParser;
    private final SadimMessageRepository repository;

    public SadimMessageServiceImpl(@Qualifier("sadimStreamApiParser") SadimJsonParser sadimJsonParser,
                                   SadimMessageRepository repository) {
        this.sadimJsonParser = sadimJsonParser;
        this.repository = repository;
    }

    @Override
    @Transactional
    public SadimMessage saveMessage(ConsumerRecord consumerRecord) {

        final var attestationParam = sadimJsonParser.getParam(consumerRecord.value().toString())
                .orElseThrow(
                        () -> new SadimJsonProcessingException("Не удалось получить параметры из сообщения от SADIM.")
                );

        log.debug("SADIM message with offset: {}; attestationParam:{}", consumerRecord.offset(), attestationParam);

        final var sadimMessage = SadimMessage.builder()
                .key(consumerRecord.key().toString())
                .partition(consumerRecord.partition())
                .offset(consumerRecord.offset())
                .ts(LocalDateTime.now())
                .param(attestationParam)
                .build();
        final var sadimFromBase = findByPartitionAndOffset(
                consumerRecord.partition(),
                consumerRecord.offset()
        );

        if (sadimFromBase.isPresent()) {
            log.debug("the message with offset: [{}]; partition: [{}] is already present in the database." +
                            " Loading data from base..."
                    , consumerRecord.offset(), consumerRecord.partition());
            return sadimFromBase.get();
        }
        return repository.save(sadimMessage);
    }

    @Override
    public List<SadimMessage> findByParamPrimeId(String primeId) {

        List<SadimMessage> messages = repository.findSadimMessagesByParam_PrimeIdOrderByTsDesc(primeId);
        log.info("messages size by primeId: {}", messages.size());

        return messages;
    }

    @Override
    public Optional<SadimMessage> findByPartitionAndOffset(Integer partition, Long offset) {
        Assert.notNull(partition, "partition must not be null");
        Assert.notNull(offset, "offset must not be null");

        return repository.findFirstByPartitionAndOffset(partition, offset);
    }
}
