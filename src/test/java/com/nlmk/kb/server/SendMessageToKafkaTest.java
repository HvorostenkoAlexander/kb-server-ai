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
    private static final String SADIM_TOPIC = "PA-MU.NLMK.P3.HSM";
    private final ObjectMapper mapper = new ObjectMapper();

    KafkaProducer<Object, Object> avroProducer;
    // одна комбинация: тема + схема (версия схемы привязана к теме!)
    private static final String CCM_TOPIC = "000-1.l3-ccm-pgp.db.Attestation-Request.0";

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
        strip.setTimeRolling(new Date(1_655_880_000_000L));
        strip.setPrimeId("0001020210329001515440422");
        strip.setT12Min(795.0);
        strip.setT12Max(835.0);
        value.setStrips(List.of(strip));
        value.setLotNo(25217); // -> hnum
        value.setMeltNo(2106684); // -> nplv

        ProducerRecord<Object, Object> record = new ProducerRecord<>(
                SADIM_TOPIC,
                // при наличие value key не анализируется
                "key~" + Instant.now().getEpochSecond(),
                mapper.writeValueAsString(value)
        );

        sendString(record);
    }

    @Test
    void sendCcmMessage() {
        nlmk.l3.ccm.pgp.RecordData data = nlmk.l3.ccm.pgp.RecordData.newBuilder()
                .setPrimeId("0001020210329001515440422")
                .setNplv(2106684) // <- meltNo
                .setHnum(25217) // <-- lotNo
                .setRoll("1-1")
                .setOrderPos(1)
                .setThickness(2.65f)
                .setWidth(1232.0f)
                .setWeightNet(10.86f)
                .setKceh(12)
                .setOrderNum(40434341L)
                .setOrderPos(4)
                // спецификация
                .setSpecifications(List.of(
                        nlmk.l3.ccm.pgp.RecordSpecifications.newBuilder().setSpecTypeCode(1)
                                .setSpecCode(3).setSpecName("Марка стали").setSpecValue("Ст3сп").build(),
                        nlmk.l3.ccm.pgp.RecordSpecifications.newBuilder().setSpecTypeCode(2)
                                .setSpecCode(45).setSpecName("Признак травления").setSpecValue("0").build()
                ))
                // ТрЗак
                .setOrderReq(List.of(
                        nlmk.l3.ccm.pgp.RecordOrderReq.newBuilder().setAttrTypeCode(1).setAttrTypeValue(1)
                                .setAttrCode(219).setAttrName("Наименование продукции")
                                .setAttrValue("Сталь тонколистовая горячекатаная").build(),
                        nlmk.l3.ccm.pgp.RecordOrderReq.newBuilder().setAttrTypeCode(1).setAttrTypeValue(1)
                                .setAttrCode(4).setAttrName("Вид продукции")
                                .setAttrValue("РУЛОН").build()
                ))
                .build();

        nlmk.l3.ccm.pgp.AttestationRequest value = nlmk.l3.ccm.pgp.AttestationRequest.newBuilder()
                .setTs("2021-05-18T00:38:25.194-03:00")
                .setOp(nlmk.l3.ccm.pgp.EnumOp.U)
                .setPk(nlmk.l3.ccm.pgp.RecordPk.newBuilder()
                        .setId("0001020210329001515440422")
                        .setSystemCode("16")
                        .build())
                .setData(data)
                .build();

        ProducerRecord<Object, Object> record = new ProducerRecord<>(
                CCM_TOPIC,
                "key~" + Instant.now().getEpochSecond(), // случайный key
                value
        );

        sendAvro(record);
    }

}
