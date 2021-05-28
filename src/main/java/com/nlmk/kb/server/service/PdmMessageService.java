package com.nlmk.kb.server.service;

import com.nlmk.kb.server.entity.pdm.PdmMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.Optional;

public interface PdmMessageService {

    public Optional<PdmMessage> saveConsumerRecord(ConsumerRecord record);

    public Optional<PdmMessage> savePdmMessage(PdmMessage message);
}
