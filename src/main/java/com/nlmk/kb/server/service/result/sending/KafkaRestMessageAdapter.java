package com.nlmk.kb.server.service.result.sending;

import com.nlmk.kb.server.api.MessagesBatchDto;
import com.nlmk.kb.server.entity.KafkaMessageKey;
import org.apache.avro.specific.SpecificRecordBase;

public interface KafkaRestMessageAdapter {

    MessagesBatchDto adapt(SpecificRecordBase specificRecord, KafkaMessageKey key);

}
