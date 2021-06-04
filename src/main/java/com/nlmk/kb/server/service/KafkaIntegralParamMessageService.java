package com.nlmk.kb.server.service;

import com.nlmk.kb.server.entity.KafkaIntegralParamMessage;

@Deprecated
public interface KafkaIntegralParamMessageService {

    public void messageProcessing(KafkaIntegralParamMessage message);
}
