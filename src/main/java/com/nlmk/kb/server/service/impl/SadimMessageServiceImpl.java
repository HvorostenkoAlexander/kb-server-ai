package com.nlmk.kb.server.service.impl;

import com.nlmk.kb.server.entity.PreAttestationParam;
import com.nlmk.kb.server.entity.SadimMessage;
import com.nlmk.kb.server.repository.SadimMessageRepository;
import com.nlmk.kb.server.service.SadimJsonParser;
import com.nlmk.kb.server.service.SadimMessageService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class SadimMessageServiceImpl implements SadimMessageService {

    private final SadimJsonParser sadimJsonParser;
    private final SadimMessageRepository sadimMessageRepository;

    public SadimMessageServiceImpl(@Qualifier("sadimStreamApiParser") SadimJsonParser sadimJsonParser,
                                   SadimMessageRepository sadimMessageRepository) {
        this.sadimJsonParser = sadimJsonParser;
        this.sadimMessageRepository = sadimMessageRepository;
    }

    @Override
    public SadimMessage saveMessage(ConsumerRecord consumerRecord) {

        Optional<PreAttestationParam> attestationParam = sadimJsonParser.getParam(consumerRecord.value().toString());
        log.debug("--- SADIM message with offset: {}; attestationParam:{}", consumerRecord.offset(), attestationParam.get());

        val sadimMessage = SadimMessage.builder()
                .key(consumerRecord.key().toString())
                .partition(consumerRecord.partition())
                .offset(consumerRecord.offset())
                .ts(LocalDateTime.now())
                .build();

        if (attestationParam.isPresent()) {
            sadimMessage.setParam(attestationParam.get());
        }

        return sadimMessageRepository.save(sadimMessage);
    }

    @Override
    public List<SadimMessage> findByParamPrimeId(String primeId) {

        List<SadimMessage> messages = sadimMessageRepository.findSadimMessagesByParam_PrimeIdOrderByTs(primeId);
        log.info("messages size by primeId: {}", messages.size());

        List<SadimMessage> messagesCheck = sadimMessageRepository.findSadimMessagesByParam_PrimeId(primeId);
        log.info("messagesCheck size by primeId: {}", messagesCheck.size());

        return messages;
    }
}
