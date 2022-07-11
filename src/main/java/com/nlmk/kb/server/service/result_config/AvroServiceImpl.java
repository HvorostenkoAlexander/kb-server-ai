package com.nlmk.kb.server.service.result_config;

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
    public <T extends SpecificRecordBase> String toJsonString(T record) throws IOException {
        SpecificDatumWriter<T> writer = new SpecificDatumWriter<>();
        writer.setSchema(record.getSchema());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        JsonEncoder jsonEncoder = EncoderFactory.get().jsonEncoder(record.getSchema(), byteArrayOutputStream);
        writer.write(record, jsonEncoder);
        jsonEncoder.flush();
        return new String(byteArrayOutputStream.toByteArray());
    }

}
