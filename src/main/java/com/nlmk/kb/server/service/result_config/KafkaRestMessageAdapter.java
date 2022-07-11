package com.nlmk.kb.server.service.result_config;

import com.nlmk.kb.server.api.MessagesBatchDto;
import com.nlmk.kb.server.entity.KafkaMessageKey;
import org.apache.avro.specific.SpecificRecordBase;

public interface KafkaRestMessageAdapter {

    MessagesBatchDto adapt(SpecificRecordBase record, KafkaMessageKey key);

}
