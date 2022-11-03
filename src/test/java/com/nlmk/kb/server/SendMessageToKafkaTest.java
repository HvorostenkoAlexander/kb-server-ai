package com.nlmk.kb.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.attestation.product.api.specification.SpecCode;
import com.nlmk.attestation.product.api.specification.TypeCode;
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
    private static final String CCM_PGP_TOPIC = "000-1.l3-ccm-pgp.db.Attestation-Request.0";
    private static final String CCM_PTS_TOPIC = "000-1.l3-ccm-pts.db.Attestation-Request.0";

    private static final String PDM_TOPIC_ASAP_MECH_PROP_DT = "000-0.l3-pdm.cdc.sp-asap-mech-properties-dt.0";
    private static final String PDM_TOPIC_PHYS_MECH_PROP_ANIS_STEEL = "000-0.l3-pdm.cdc.sp-phys-mech-prop-anis-steel-stand.0";

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
    void sendCcmPgpMessage() {
        nlmk.l3.ccm.pgp.RecordData data = nlmk.l3.ccm.pgp.RecordData.newBuilder()
                .setPrimeId("0001020210329001515440422")
                .setNplv(2106684) // <- meltNo
                .setHnum(25217) // <-- lotNo
                .setRoll("1-1")
                .setThickness(2.65f)
                .setWidth(1232.0f)
                .setWeightNet(10.86f)
                .setKceh(12)
                .setOrderNum(40L)
                .setOrderPos(4)
                .setSpecifications(List.of(
                        nlmk.l3.ccm.pgp.RecordSpecifications.newBuilder().setSpecTypeCode(1)
                                .setSpecCode(3).setSpecName("Марка стали").setSpecValue("Ст3сп").build(),
                        nlmk.l3.ccm.pgp.RecordSpecifications.newBuilder().setSpecTypeCode(2)
                                .setSpecCode(45).setSpecName("Признак травления").setSpecValue("0").build()
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
                CCM_PGP_TOPIC,
                "key~" + Instant.now().getEpochSecond(), // случайный key
                value
        );

        sendAvro(record);
    }

    @Test
    void sendCcmPtsMessage() {
        nlmk.l3.ccm.pts.RecordData data = nlmk.l3.ccm.pts.RecordData.newBuilder()
                .setPrimeId("42")
                .setNplv(2106684) // <- meltNo
                .setHnum(25217) // <-- lotNo
                .setRoll("1-1")
                .setThickness(2.65f)
                .setWidth(1232.0f)
                .setWeightNet(10.86f)
                .setKceh(12)
                .setOrderNum(40434341)
                .setOrderPos(4)
                // .. будут еще поля
                .build();

        nlmk.l3.ccm.pts.AttestationRequest value = nlmk.l3.ccm.pts.AttestationRequest.newBuilder()
                .setTs("2022-06-27T16:45:25.000+05:00")
                .setOp(nlmk.l3.ccm.pts.EnumOp.U)
                .setPk(nlmk.l3.ccm.pts.RecordPk.newBuilder()
                        .setId("42")
                        .setSystemCode("16")
                        .build())
                .setData(data)
                .build();

        ProducerRecord<Object, Object> record = new ProducerRecord<>(
                CCM_PTS_TOPIC,
                "key~" + Instant.now().getEpochSecond(), // случайный key
                value
        );

        sendAvro(record);
    }

    @Test
    void sendPdmSpAsapMechPropertiesDt() {
        nlmk.l3.pdm.SpAsapMechPropertiesDt value = nlmk.l3.pdm.SpAsapMechPropertiesDt.newBuilder()
                .setTs("2022-07-25T15:25:25.123+05:00")
                .setOp(nlmk.l3.pdm.opEnum.I) // I -> U -> D
                .setPk(nlmk.l3.pdm.Pk.newBuilder()
                        .setId("42")
                        .setSystemCode("16")
                        .setDirectoryId("4242")
                        .build())
                .setData(nlmk.l3.pdm.Data.newBuilder()
                        .setSpecifications(List.of(
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.STEEL_MARK.getValue())
                                        .setSpecName(SpecCode.STEEL_MARK.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("NV23S-95L").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.ADDITIONAL_REQUIREMENTS.getValue())
                                        .setSpecName(SpecCode.ADDITIONAL_REQUIREMENTS.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("ДТ 0043").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.PRODUCT_STANDARD.getValue())
                                        .setSpecName(SpecCode.PRODUCT_STANDARD.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("СТО 05757665-008-2019").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.P1750SST.getValue())
                                        .setSpecName(SpecCode.P1750SST.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("*..0.95").build()
                        ))
                        .build())
                .build();

        ProducerRecord<Object, Object> record = new ProducerRecord<>(
                PDM_TOPIC_ASAP_MECH_PROP_DT,
                "key~" + Instant.now().getEpochSecond(), // случайный key
                value
        );

        sendAvro(record);
    }

    @Test
    void sendPdmSpPhysMechPropAnisSteelStand() {
        nlmk.l3.pdm.SpPhysMechPropAnisSteelStand value = nlmk.l3.pdm.SpPhysMechPropAnisSteelStand.newBuilder()
                .setTs("2022-07-25T15:35:35.321+05:00")
                .setOp(nlmk.l3.pdm.opEnum.I) // I -> U -> D
                .setPk(nlmk.l3.pdm.Pk.newBuilder()
                        .setId("44")
                        .setSystemCode("16")
                        .setDirectoryId("4444")
                        .build())
                .setData(nlmk.l3.pdm.Data.newBuilder()
                        .setSpecifications(List.of(
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.STEEL_MARK.getValue())
                                        .setSpecName(SpecCode.STEEL_MARK.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("Ст3сп").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.PRODUCT_STANDARD.getValue())
                                        .setSpecName(SpecCode.PRODUCT_STANDARD.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("ГОСТ 21427.4-78").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.THICKNESS_PRODUCTS.getValue())
                                        .setSpecName(SpecCode.THICKNESS_PRODUCTS.getDesc())
                                        .setSpecTypeCode(TypeCode.NUMBER.getValue()).setSpecMeasure("x")
                                        .setSpecValue("2.65").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.P15400.getValue())
                                        .setSpecName(SpecCode.P15400.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("*..23.0").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.PLASTICITY_NUMBER_OF_BENDS.getValue())
                                        .setSpecName(SpecCode.PLASTICITY_NUMBER_OF_BENDS.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("2..*").build()
                        ))
                        .build())
                .build();

        ProducerRecord<Object, Object> record = new ProducerRecord<>(
                PDM_TOPIC_PHYS_MECH_PROP_ANIS_STEEL,
                "key~" + Instant.now().getEpochSecond(), // случайный key
                value
        );

        sendAvro(record);
    }

}
