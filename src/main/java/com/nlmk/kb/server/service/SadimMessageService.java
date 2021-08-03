package com.nlmk.kb.server.service;

import com.nlmk.kb.server.entity.PreAttestationParam;
import com.nlmk.kb.server.entity.SadimMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.List;
import java.util.Optional;


public interface SadimMessageService {

    public SadimMessage saveMessage(ConsumerRecord consumerRecord);

    public List<SadimMessage> findByParamPrimeId(String primeId);

    public Optional<SadimMessage> findByPartitionAndOffset(Integer partition, Long offset);
}
