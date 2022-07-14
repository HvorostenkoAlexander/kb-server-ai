package com.nlmk.kb.server.service.result.sending;

import org.apache.avro.specific.SpecificRecordBase;

import java.io.IOException;

public interface AvroService {

    <T extends SpecificRecordBase> String toJsonString(T specificRecord) throws IOException;

}
