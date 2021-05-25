package com.nlmk.kb.server.service;

import com.nlmk.kb.server.entity.PreAttestationParam;
import com.nlmk.kb.server.entity.SadimMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.List;


public interface SadimMessageService {

    public SadimMessage saveMessage(ConsumerRecord consumerRecord);
    public List<SadimMessage> findByParamPrimeId(String primeId);
}
