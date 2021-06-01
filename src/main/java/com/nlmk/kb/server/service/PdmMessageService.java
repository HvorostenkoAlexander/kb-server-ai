package com.nlmk.kb.server.service;

import com.nlmk.kb.server.entity.pdm.PdmMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.Optional;

public interface PdmMessageService {

    public Optional<PdmMessage> save(PdmMessage message);

    public Optional<PdmMessage> update(PdmMessage message);
}
