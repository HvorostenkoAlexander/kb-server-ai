package com.nlmk.kb.server.service.result.sending;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonEncoder;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
@Service
public class AvroServiceImpl implements AvroService {

    @Override
    public <T extends SpecificRecordBase> String toJsonString(T specificRecord) throws IOException {
        SpecificDatumWriter<T> writer = new SpecificDatumWriter<>();
        writer.setSchema(specificRecord.getSchema());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        JsonEncoder jsonEncoder = EncoderFactory.get().jsonEncoder(specificRecord.getSchema(), byteArrayOutputStream);
        writer.write(specificRecord, jsonEncoder);
        jsonEncoder.flush();
        return new String(byteArrayOutputStream.toByteArray());
    }

}
