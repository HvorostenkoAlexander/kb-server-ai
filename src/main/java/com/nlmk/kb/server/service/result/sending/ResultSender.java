package com.nlmk.kb.server.service.result.sending;

import org.apache.avro.specific.SpecificRecordBase;

public interface ResultSender {

    void send(SpecificRecordBase result, String topic, String key);

}
