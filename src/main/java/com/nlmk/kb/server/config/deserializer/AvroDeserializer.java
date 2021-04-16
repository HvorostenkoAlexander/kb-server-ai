package com.nlmk.kb.server.config.deserializer;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import javax.xml.bind.DatatypeConverter;
import java.util.Arrays;
import java.util.Map;

@Slf4j
public class AvroDeserializer<T extends SpecificRecordBase> implements Deserializer<T> {

    protected final Class<T> targetType;

    public AvroDeserializer(Class<T> targetType) {
        this.targetType = targetType;
    }

    @Override
    public void close() {
    }

    @Override
    public void configure(Map<String, ?> arg0, boolean arg1) {
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        try {
            T result = null;

            if (data != null) {
                log.info("data='{}'", DatatypeConverter.printHexBinary(data));

                DatumReader<GenericRecord> datumReader =
                        new SpecificDatumReader<>(targetType.getDeclaredConstructor().newInstance().getSchema());
                log.info("--- datumReader: "+datumReader.toString());

                Decoder decoder = DecoderFactory.get().binaryDecoder(data, null);
                log.info("--- decoder: "+decoder.toString());
                log.info("--- decoder.readString"+decoder.readString());

                result = (T) datumReader.read(null, decoder);
                log.info("deserialized data='{}'", result);
            }
            return result;
        } catch (Exception ex) {

          //  Arrays.stream(ex.getStackTrace()).forEach(el -> log.error("--- stackTrace: " + el));

            throw new SerializationException(
                  //  "Can't deserialize data '" + Arrays.toString(data) + "' from topic '" + topic + "'", ex);
                    "Can't deserialize data '" + "' from topic '" + topic + "'"+"exception: "+ex.toString(), ex);
        }
    }
}