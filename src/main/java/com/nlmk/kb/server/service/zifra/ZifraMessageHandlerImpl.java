package com.nlmk.kb.server.service.zifra;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ZifraMessageHandlerImpl implements ZifraMessageHandler {

    @Override
    public boolean handleConsumerRecord(ConsumerRecord<Object, Object> consumerRecord) {
        // todo
        return true;
    }

}
