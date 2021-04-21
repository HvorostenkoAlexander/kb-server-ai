package com.nlmk.kb.server.service;

import com.nlmk.kb.server.entity.KafkaUnrecoverableParamMessage;

public interface KafkaUnrecoverableParamMessageService {

    public void messageProcessing(KafkaUnrecoverableParamMessage message);
}
