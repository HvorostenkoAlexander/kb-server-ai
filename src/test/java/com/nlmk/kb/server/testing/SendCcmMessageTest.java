package com.nlmk.kb.server.testing;

import com.nlmk.attestation.product.api.specification.SpecCode;
import com.nlmk.attestation.product.api.specification.TypeCode;
import nlmk.l3.ccm.pgp.RecordMetallographic;
import nlmk.l3.ccm.pgp.RecordMetgrapAnalysisData;
import nlmk.l3.ccm.pgp.RecordMetgrapData;
import nlmk.nlmk.l3.ccm.pts.db.attestation.request.ver1.*;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.*;

import java.util.List;

@Disabled("hand sender")
class SendCcmMessageTest extends SendMessageToKafka {

    // одна комбинация: тема + схема (версия схемы привязана к теме!)
    private static final String CCM_PGP_TOPIC = "000-1.l3-ccm-pgp.db.Attestation-Request.0";
    private static final String CCM_PTS_TOPIC = "000-1.l3-ccm-pts.db.Attestation-Request.0";
    private static final String CCM_KC1_TOPIC = "000-1.l3-sus-kc1.db.attest-request.0";
    private static final String CCM_KC2_TOPIC = "000-1.l3-sus-kc2.db.attest-request.0";

    @Test
    void sendCcmPgpMessage() {
        // attestationPoint(0) Аттестация после стана, -- 12 (ЦГП) attestationPointOrder(1)
        // attestationPoint(1) Аттестация перед резкой, - 12 (ЦГП) attestationPointOrder(2)
        // attestationPoint(2) Аттестация по ФПК,       - 12 (ЦГП) attestationPointOrder(3)

        nlmk.l3.ccm.pgp.RecordData data = nlmk.l3.ccm.pgp.RecordData.newBuilder()
                .setPrimeId("task-1270")
                .setNplv(2106684) // <- meltNo
                .setHnum(25217) // <-- lotNo
                .setRoll("1-1")
                .setThickness(2.65f)
                .setWidth(1232.0f)
                .setWeightNet(10.86f)
                .setKceh(12)
                .setOrderNum(1270L) // заказ должен быть загружен
                .setOrderPos(3)
                .setAttestationPoint(0)
                .setCutTaskNum(100)
                .setCutTaskStrNum(1)
                .setCutTaskDate("2022-11-24")
                .setSpecifications(List.of(
                        nlmk.l3.ccm.pgp.RecordSpecifications.newBuilder().setSpecTypeCode(1)
                                .setSpecCode(3).setSpecName("Марка стали").setSpecValue("11ЮА").build(),
                        nlmk.l3.ccm.pgp.RecordSpecifications.newBuilder().setSpecTypeCode(2)
                                .setSpecCode(45).setSpecName("Признак травления").setSpecValue("0").build()
                ))
                .setMetallographic(List.of(
                        RecordMetallographic.newBuilder()
                                .setHnum(25217) // <--
                                .setMetgrapData(List.of(
                                        RecordMetgrapData.newBuilder().setMetgrapAnalysisId(100)
                                                .setMetgrapAnalysisData(List.of(
                                                        RecordMetgrapAnalysisData.newBuilder().setMetgrapCode(86)
                                                                .setMetgrapName("УФС, балл перлит")
                                                                .setMetgrapTypeCode(2).setMetgrapValue("1")
                                                                .build(),
                                                        RecordMetgrapAnalysisData.newBuilder().setMetgrapCode(89)
                                                                .setMetgrapName("Полосчатость")
                                                                .setMetgrapTypeCode(2).setMetgrapValue("2")
                                                                .build()
                                                ))
                                                .build()
                                ))
                                .build()
                ))
                .build();

        nlmk.l3.ccm.pgp.AttestationRequest value = nlmk.l3.ccm.pgp.AttestationRequest.newBuilder()
                .setTs("2022-11-24T13:00:25.194+05:00")
                .setOp(nlmk.l3.ccm.pgp.EnumOp.U)
                .setPk(nlmk.l3.ccm.pgp.RecordPk.newBuilder()
                        .setId("task-1270")
                        .setSystemCode("16")
                        .build())
                .setData(data)
                .build();

        ProducerRecord<Object, Object> record = new ProducerRecord<>(CCM_PGP_TOPIC, randomKey(), value);

        Assertions.assertDoesNotThrow(() -> sendAvro(record));
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
                        .setSystemCode("14")
                        .build())
                .setData(data)
                .build();

        ProducerRecord<Object, Object> record = new ProducerRecord<>(CCM_PTS_TOPIC, randomKey(), value);

        Assertions.assertDoesNotThrow(() -> sendAvro(record));
    }

    @Test
    void sendCcmKc1Message() {
        nlmk.l3.sus.kc1.RecordData data = nlmk.l3.sus.kc1.RecordData.newBuilder()
                .setId("slabID")
                .setWerks(1).setWerksName("1")
                .setKceh(6).setKcehName("КЦ-1")
                .setOrderNum(1413L).setOrderPos(6)
                .setMarking(nlmk.l3.sus.kc1.RecordMarking.newBuilder()
                        .setHeat(2106684)
                        .setStrand(25217)
                        .setSlab(1)
                        .build())
                .setMarkingAcc(nlmk.l3.sus.kc1.RecordMarkingAcc.newBuilder()
                        .setHeat(2106684)
                        .setStrand(25217)
                        .setSlab(1)
                        .build())
                .setSpecifications(List.of(
                        nlmk.l3.sus.kc1.RecordDataSpecifications.newBuilder()
                                .setSpecCode(SpecCode.VACUUMING.getValue())
                                .setSpecName(SpecCode.VACUUMING.getDesc())
                                .setSpecValue("RH")
                                .setSpecTypeCode(TypeCode.STRING.getValue())
                                .setSpecTypeValue(1)
                                .setSpecTypeName("x")
                                .setSpecMeasure("x")
                                .build()
                ))
                .setPlanTask(nlmk.l3.sus.kc1.RecordPlanTask.newBuilder().setPlanTaskId("1").setPlanTaskLineId("11").build())
                .setRequirements(nlmk.l3.sus.kc1.RecordRequirements.newBuilder()
                        .build())
                .build();

        nlmk.l3.sus.kc1.AttestRequest value = nlmk.l3.sus.kc1.AttestRequest.newBuilder()
                .setTs("2022-12-27T14:36:25.000+05:00")
                .setOp(nlmk.l3.sus.kc1.EnumOp.U)
                .setPk(nlmk.l3.sus.kc1.RecordPk.newBuilder()
                        .setId("42") // primeId
                        .setSystemCode("12")
                        .build())
                .setData(data)
                .build();

        ProducerRecord<Object, Object> record = new ProducerRecord<>(CCM_KC1_TOPIC, randomKey(), value);

        Assertions.assertDoesNotThrow(() -> sendAvro(record));
    }

    @Test
    void sendCcmKc2Message() {
        nlmk.l3.sus.kc2.RecordData data = nlmk.l3.sus.kc2.RecordData.newBuilder()
                .setId("slabID")
                .setWerks(1).setWerksName("1")
                .setKceh(7).setKcehName("КЦ-2")
                .setOrderNum(1413L).setOrderPos(7)
                .setMarking(nlmk.l3.sus.kc2.RecordMarking.newBuilder()
                        .setHeat(2106684)
                        .setStrand(25217)
                        .setSlab(1)
                        .build())
                .setMarkingAcc(nlmk.l3.sus.kc2.RecordMarkingAcc.newBuilder()
                        .setHeat(2106684)
                        .setStrand(25217)
                        .setSlab(1)
                        .build())
                .setSpecifications(List.of(
                        nlmk.l3.sus.kc2.RecordDataSpecifications.newBuilder()
                                .setSpecCode(SpecCode.VACUUMING.getValue())
                                .setSpecName(SpecCode.VACUUMING.getDesc())
                                .setSpecValue("RH")
                                .setSpecTypeCode(TypeCode.STRING.getValue())
                                .setSpecTypeValue(1)
                                .setSpecTypeName("x")
                                .setSpecMeasure("x")
                                .build()
                ))
                .setPlanTask(nlmk.l3.sus.kc2.RecordPlanTask.newBuilder().setPlanTaskId("1").setPlanTaskLineId("11").build())
                .setRequirements(nlmk.l3.sus.kc2.RecordRequirements.newBuilder()
                        .build())
                .build();

        nlmk.l3.sus.kc2.AttestRequest value = nlmk.l3.sus.kc2.AttestRequest.newBuilder()
                .setTs("2022-12-27T14:36:25.000+05:00")
                .setOp(nlmk.l3.sus.kc2.EnumOp.U)
                .setPk(nlmk.l3.sus.kc2.RecordPk.newBuilder()
                        .setId("42") // primeId
                        .setSystemCode("13")
                        .build())
                .setData(data)
                .build();

        ProducerRecord<Object, Object> record = new ProducerRecord<>(CCM_KC2_TOPIC, randomKey(), value);

        Assertions.assertDoesNotThrow(() -> sendAvro(record));
    }

}
