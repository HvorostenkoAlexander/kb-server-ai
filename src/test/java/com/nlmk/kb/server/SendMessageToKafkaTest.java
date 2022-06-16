package com.nlmk.kb.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nlmk.sadim.Sadim;
import nlmk.sadim.Strip;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.*;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@Disabled("hand sender")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SendMessageToKafkaTest {

    KafkaProducer<Object, Object> stringProducer;
    private final String sadimTopic = "PA-MU.NLMK.P3.HSM";
    private final ObjectMapper mapper = new ObjectMapper();

    KafkaProducer<Object, Object> avroProducer;
    // одна комбинация: тема + схема (версия схемы привязана к теме!)
    private final String ccmTopic = "000-1.l3-ccm-pgp.db.Attestation-Request.0";

    @BeforeAll
    void setUp() {
        final var avroProps = new Properties();
        avroProps.put("bootstrap.servers", "localhost:29092");
        avroProps.put("schema.registry.url", "http://localhost:28881");
        avroProps.put("key.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
        avroProps.put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
        avroProducer = new KafkaProducer<>(avroProps);

        final var stringProps = new Properties();
        stringProps.put("bootstrap.servers", "localhost:29092");
        stringProps.put("schema.registry.url", "http://localhost:28881");
        stringProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        stringProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        stringProducer = new KafkaProducer<>(stringProps);
    }

    private void sendAvro(ProducerRecord<Object, Object> record) {
        try {
            // синхронная отправка сообщения
            final var task = avroProducer.send(record).get();
            System.out.printf("Sent Message, Offset %d%n", task.offset());
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail();
        }
        avroProducer.close();
    }

    private void sendString(ProducerRecord<Object, Object> record) {
        try {
            // синхронная отправка сообщения
            final var task = stringProducer.send(record).get();
            System.out.printf("Sent Message, Offset %d%n", task.offset());
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail();
        }
        stringProducer.close();
    }

    @Test
    void sendSadimMessage() throws JsonProcessingException {
        // nlmk.sadim.Sadim класс составленный на основе примеров сообщений САДиМ, полученных как JSON.
        final var value = new Sadim();
        final var strip = new Strip();
        strip.setTimeRolling(new Date(1_000_000_000L));
        strip.setPrimeId("pi100");
        strip.setT12Min(1.0);
        strip.setT12Max(2.0);
        value.setStrips(List.of(strip));
        value.setLotNo(12);
        value.setMeltNo(13);

        ProducerRecord<Object, Object> record = new ProducerRecord<>(
                sadimTopic,
                // при наличие value key не анализируется
                "key~" + Instant.now().getEpochSecond(),
                mapper.writeValueAsString(value)
        );

        sendString(record);
    }

    @Test
    void sendCcmMessage() {
        nlmk.l3.ccm.pgp.RecordData data = nlmk.l3.ccm.pgp.RecordData.newBuilder()
                .setPrimeId("prime12")
                .setOrderPos(1)
                .setRoll("r2")
                .setThickness(1.2f)
                .setWidth(1.3f)
                .setWeightNet(1.4f)
                .setKceh(3)
                .setSpecifications(List.of())
                .build();

        nlmk.l3.ccm.pgp.AttestationRequest value = nlmk.l3.ccm.pgp.AttestationRequest.newBuilder()
                .setTs("2021-04-08T10:35:25.452-03:00")
                .setOp(nlmk.l3.ccm.pgp.EnumOp.I)
                .setPk(nlmk.l3.ccm.pgp.RecordPk.newBuilder()
                        .setId("0001020111023170548152095")
                        .setSystemCode("16")
                        .build())
                .setData(data)
                .build();

        ProducerRecord<Object, Object> record = new ProducerRecord<>(
                ccmTopic,
                "key~" + Instant.now().getEpochSecond(), // случайный key
                value
        );

        sendAvro(record);
    }

}
