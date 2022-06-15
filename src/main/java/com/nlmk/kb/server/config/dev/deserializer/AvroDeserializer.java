package com.nlmk.kb.server.config.dev.deserializer;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.serialization.Deserializer;

@Slf4j
public class AvroDeserializer<T extends SpecificRecordBase> implements Deserializer<T> {

    protected final Class<T> targetType;

    public AvroDeserializer(Class<T> targetType) {
        this.targetType = targetType;
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        T result = null;
        try {
            if (data != null) {
                DatumReader<GenericRecord> datumReader =
                        new SpecificDatumReader<>(targetType.getDeclaredConstructor().newInstance().getSchema());

                Decoder decoder = DecoderFactory.get().binaryDecoder(data, null);

                result = (T) datumReader.read(null, decoder);
            }
        } catch (Exception ex) {
            // запись в журнал ошибки десериализации
            log.error("deserialize exception for topic [{}]", topic, ex);
        }
        // пустой результат
        return result;
    }

}