package com.nlmk.kb.server.service;

import com.nlmk.kb.server.entity.pdm.PdmMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface PdmMessageCreator {

    String getType();

    PdmMessage createPdmMessage(ConsumerRecord record);
}
