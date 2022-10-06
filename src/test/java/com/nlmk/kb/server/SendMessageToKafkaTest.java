package com.nlmk.kb.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.attestation.product.api.specification.SpecCode;
import com.nlmk.attestation.product.api.specification.TypeCode;
import nlmk.nlmk.l3.ccm.pts.db.attestation.request.ver1.*;
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
    private static final String PDM_TOPIC_SP_TOL_EVENNESS_DT = "000-0.l3-pdm.cdc.sp-tol-evenness-dt.0";
    private static final String PDM_TOPIC_SP_TOL_THICK_DT = "000-0.l3-pdm.cdc.sp-tol-thick-dt.0";
    private static final String PDM_TOPIC_SP_TOL_WIDTH_DT = "000-0.l3-pdm.cdc.sp-tol-width-dt.0";

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
        nlmk.nlmk.l3.ccm.pts.db.attestation.request.ver1.RecordData data = RecordData.newBuilder()
                .setWerks(1).setWerksName("1")
                .setKceh(11).setKcehName("11")
                .setUnitCode(2).setUnitName("2")
                .setStorageCode(3).setStorageName("3")
                .setWeightNet(10.86f)
                .setKceh(11)
                .setOrderNum(1014L)
                .setOrderPos(1)
                .setMarking(RecordMarking.newBuilder()
                        .setNplv(2106684) // <- meltNo
                        .setHnum(25217) // <-- lotNo
                        .setTnum(1)
                        .setRoll(1)
                        .build())
                .setGeometry(RecordGeometry.newBuilder()
                        .setThickness(2.65f)
                        .setWidth(1232.0f)
                        .build())
                .setSpecifications(List.of())
                .setBundles(List.of(
                        RecordBundles.newBuilder()
                                .setStripId(1).setStripNum(1).setStripWidth(1f).setStripWeight(2.3f).build(),
                        RecordBundles.newBuilder()
                                .setStripId(2).setStripNum(2).setStripWidth(2f).setStripWeight(2.5f).build(),
                        RecordBundles.newBuilder()
                                .setStripId(3).setStripNum(3).setStripWidth(3f).setStripWeight(5.2f).build()
                ))
                .setProperties(List.of(
                        RecordProperties.newBuilder()
                                .setProbeCode(3).setProbeName("3").setTestDate("3")
                                .setTypeCode(3).setTypeName("3")
                                .setAnalyzes(List.of())
                                .setAttestationList(List.of())
                                .setListValues(List.of(
                                        RecordDataPropertiesListValues.newBuilder()
                                                .setAttrCode(3)
                                                .setAttrValue(List.of(
                                                        RecordDataPropertiesListValuesAttrValue.newBuilder().setValue("3").build()
                                                )).setAttrType(1)
                                                .build(),
                                        RecordDataPropertiesListValues.newBuilder()
                                                .setAttrCode(SpecCode.PLASTICITY_NUMBER_OF_BENDS.getValue())
                                                .setAttrType(SpecCode.PLASTICITY_NUMBER_OF_BENDS.getTypeCode().getValue())
                                                .setAttrValue(List.of(
                                                        RecordDataPropertiesListValuesAttrValue.newBuilder().setValue("4").build()
                                                )).build()
                                ))
                                .build()
                ))
                .build();

        nlmk.nlmk.l3.ccm.pts.DbAttestationRequestVer1 value = nlmk.nlmk.l3.ccm.pts.DbAttestationRequestVer1.newBuilder()
                .setTs("2022-09-02T14:36:25.000+05:00")
                .setOp(nlmk.EnumOp.U)
                .setPk(nlmk.nlmk.l3.ccm.pts.db.attestation.request.ver1.PkType.newBuilder()
                        .setId("42") // primeId
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

    @Test
    void sendPdmSpTolEvennessDt() {
        nlmk.l3.pdm.SpTolEvennessDt value = nlmk.l3.pdm.SpTolEvennessDt.newBuilder()
                .setTs("2022-07-25T15:35:35.321+05:00")
                .setOp(nlmk.l3.pdm.opEnum.I) // I -> U -> D
                .setPk(nlmk.l3.pdm.Pk.newBuilder()
                        .setId("41")
                        .setSystemCode("14")
                        .setDirectoryId("4141")
                        .build())
                .setData(nlmk.l3.pdm.Data.newBuilder()
                        .setSpecifications(List.of(
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.PRODUCT_STANDARD.getValue())
                                        .setSpecName(SpecCode.PRODUCT_STANDARD.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("IS 3024:2015").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.ADDITIONAL_REQUIREMENTS.getValue())
                                        .setSpecName(SpecCode.ADDITIONAL_REQUIREMENTS.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("ДТ 37.06").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.WHIDTH_PRODUCT.getValue())
                                        .setSpecName(SpecCode.WHIDTH_PRODUCT.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("(150..*").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.EVENNESS_TOLERANCE.getValue())
                                        .setSpecName(SpecCode.EVENNESS_TOLERANCE.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("3.0").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.EVENNESS_TOLERANCE_PERCENT.getValue())
                                        .setSpecName(SpecCode.EVENNESS_TOLERANCE_PERCENT.getDesc())
                                        .setSpecTypeCode(TypeCode.NUMBER.getValue()).setSpecMeasure("x")
                                        .setSpecValue("1.5").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.NOTE.getValue())
                                        .setSpecName(SpecCode.NOTE.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("TEST").build()
                        ))
                        .build())
                .build();

        ProducerRecord<Object, Object> record = new ProducerRecord<>(
                PDM_TOPIC_SP_TOL_EVENNESS_DT,
                "key~" + Instant.now().getEpochSecond(), // случайный key
                value
        );

        sendAvro(record);
    }

    @Test
    void sendPdmSpTolThickDt() {
        nlmk.l3.pdm.SpTolThickDt value = nlmk.l3.pdm.SpTolThickDt.newBuilder()
                .setTs("2022-07-25T15:35:35.321+05:00")
                .setOp(nlmk.l3.pdm.opEnum.I) // I -> U -> D
                .setPk(nlmk.l3.pdm.Pk.newBuilder()
                        .setId("41")
                        .setSystemCode("14")
                        .setDirectoryId("4141")
                        .build())
                .setData(nlmk.l3.pdm.Data.newBuilder()
                        .setSpecifications(List.of(
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.PRODUCT_STANDARD.getValue())
                                        .setSpecName(SpecCode.PRODUCT_STANDARD.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("IS 3024:2015").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.ADDITIONAL_REQUIREMENTS.getValue())
                                        .setSpecName(SpecCode.ADDITIONAL_REQUIREMENTS.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("ДТ 157.00").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.UNEVEN_GAUGE.getValue())
                                        .setSpecName(SpecCode.UNEVEN_GAUGE.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("*..0.015").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.LONG_THICK_DIFF.getValue())
                                        .setSpecName(SpecCode.LONG_THICK_DIFF.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("*..0.016").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.THICKNESS_TOLERANCE_PERCENT_MAX.getValue())
                                        .setSpecName(SpecCode.THICKNESS_TOLERANCE_PERCENT_MAX.getDesc())
                                        .setSpecTypeCode(TypeCode.NUMBER.getValue()).setSpecMeasure("x")
                                        .setSpecValue("0.020").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.THICKNESS_TOLERANCE_PERCENT_MIN.getValue())
                                        .setSpecName(SpecCode.THICKNESS_TOLERANCE_PERCENT_MIN.getDesc())
                                        .setSpecTypeCode(TypeCode.NUMBER.getValue()).setSpecMeasure("x")
                                        .setSpecValue("0.020").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.NOTE.getValue())
                                        .setSpecName(SpecCode.NOTE.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("TEST").build()
                        ))
                        .build())
                .build();

        ProducerRecord<Object, Object> record = new ProducerRecord<>(
                PDM_TOPIC_SP_TOL_THICK_DT,
                "key~" + Instant.now().getEpochSecond(), // случайный key
                value
        );

        sendAvro(record);
    }

    @Test
    void sendPdmSpTolWidthDt() {
        nlmk.l3.pdm.SpTolWidthDt value = nlmk.l3.pdm.SpTolWidthDt.newBuilder()
                .setTs("2022-07-25T15:35:35.321+05:00")
                .setOp(nlmk.l3.pdm.opEnum.I) // I -> U -> D
                .setPk(nlmk.l3.pdm.Pk.newBuilder()
                        .setId("41")
                        .setSystemCode("14")
                        .setDirectoryId("4141")
                        .build())
                .setData(nlmk.l3.pdm.Data.newBuilder()
                        .setSpecifications(List.of(
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.PRODUCT_STANDARD.getValue())
                                        .setSpecName(SpecCode.PRODUCT_STANDARD.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("IS 3024:2015").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.STEEL_MARK.getValue())
                                        .setSpecName(SpecCode.STEEL_MARK.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("Ст3сп").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.ADDITIONAL_REQUIREMENTS.getValue())
                                        .setSpecName(SpecCode.ADDITIONAL_REQUIREMENTS.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("ДТ 157.00").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.WHIDTH_PRODUCT.getValue())
                                        .setSpecName(SpecCode.WHIDTH_PRODUCT.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("(1000..1020]").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.BURR.getValue())
                                        .setSpecName(SpecCode.BURR.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("*..0.025").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.SICKLE_SHAPE.getValue())
                                        .setSpecName(SpecCode.SICKLE_SHAPE.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("*..0.9").build(),
//                                nlmk.l3.pdm.Spec.newBuilder()
//                                        .setSpecCode(SpecCode.THICKNESS_TOLERANCE_PERCENT_MAX.getValue())
//                                        .setSpecName(SpecCode.THICKNESS_TOLERANCE_PERCENT_MAX.getDesc())
//                                        .setSpecTypeCode(TypeCode.NUMBER.getValue()).setSpecMeasure("x")
//                                        .setSpecValue("0.020").build(),
//                                nlmk.l3.pdm.Spec.newBuilder()
//                                        .setSpecCode(SpecCode.THICKNESS_TOLERANCE_PERCENT_MIN.getValue())
//                                        .setSpecName(SpecCode.THICKNESS_TOLERANCE_PERCENT_MIN.getDesc())
//                                        .setSpecTypeCode(TypeCode.NUMBER.getValue()).setSpecMeasure("x")
//                                        .setSpecValue("0.020").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.NOTE.getValue())
                                        .setSpecName(SpecCode.NOTE.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("TEST").build()
                        ))
                        .build())
                .build();

        ProducerRecord<Object, Object> record = new ProducerRecord<>(
                PDM_TOPIC_SP_TOL_WIDTH_DT,
                "key~" + Instant.now().getEpochSecond(), // случайный key
                value
        );

        sendAvro(record);
    }
}
