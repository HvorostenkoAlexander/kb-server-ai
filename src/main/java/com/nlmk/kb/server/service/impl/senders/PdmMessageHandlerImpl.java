package com.nlmk.kb.server.service.impl.senders;

import com.nlmk.kb.server.service.PdmMessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdmMessageHandlerImpl implements PdmMessageHandler {


    @Override
    public boolean handleConsumerRecord(ConsumerRecord record) {
        return false;
    }
}
