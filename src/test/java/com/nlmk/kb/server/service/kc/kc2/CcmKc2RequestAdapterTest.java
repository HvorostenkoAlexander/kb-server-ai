package com.nlmk.kb.server.service.kc.kc2;

import com.nlmk.kb.server.api.ccm.SpecTypeCode;
import com.nlmk.kb.server.api.ccm.SpecTypeValue;
import com.nlmk.kb.server.api.ccm.kc.Pk;
import com.nlmk.kb.server.api.ccm.kc.request.*;
import com.nlmk.kb.server.service.ccm.kc.kc2.CcmKc2KafkaRequestAdapterImpl;
import com.nlmk.kb.server.service.ccm.kc.kc2.CcmKc2RestRequestAdapterImpl;
import java.math.BigDecimal;
import java.util.List;
import nlmk.EnumOp;
import nlmk.nlmk.l3.sus.kc2.DbAttestRequestVer0;
import nlmk.nlmk.l3.sus.kc2.db.attestrequest.ver0.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CcmKc2RequestAdapterTest {

    @Autowired
    private CcmKc2KafkaRequestAdapterImpl kafkaRequestAdapter;

    @Autowired
    private CcmKc2RestRequestAdapterImpl restRequestAdapter;

    @Test
    void testRequest() {
        var kafkaRequest = DbAttestRequestVer0.newBuilder()
                .setTs("2023-04-03T06:59:12.426-03:00")
                .setOp(EnumOp.I)
                .setPk(PkType.newBuilder()
                        .setSystemCode("7")
                        .setId("12345")
                        .build())
                .setData(RecordData.newBuilder()
                        .setPrimeId("12345")
                        .setWerks(1)
                        .setWerksName("werksName-1")
                        .setWorkshop(7)
                        .setWorkshopName("КЦ2")
                        .setOrderNum(123L)
                        .setOrderPos(1)
                        .setUnitCode("11")
                        .setUnitName("УНРС-11")
                        .setWeightNet(4.16f)
                        .setMarking(RecordMarking.newBuilder()
                                .setHeat(1)
                                .setStrand(2)
                                .setSlab(3)
                                .build())
                        .setMarkingAcc(RecordMarkingAcc.newBuilder()
                                .setHeat(1)
                                .setStrand(2)
                                .setSlab(3)
                                .build())
                        .setRequirements(RecordRequirements.newBuilder()
                                .setPlanTask(RecordPlanTask.newBuilder()
                                        .setPlanTaskId(1)
                                        .setPlanTaskLineId(2)
                                        .build())
                                .setSpecifications(List.of(
                                        RecordDataRequirementsSpecifications.newBuilder()
                                                .setSpecCode(40)
                                                .setSpecValue("2.5..2.7")
                                                .setSpecName("Замер толщины - середина")
                                                .setSpecTypeCode(2)
                                                .setSpecTypeValue(1)
                                                .setSpecTypeName("Число")
                                                .setSpecDecryption("SpecDecryption")
                                                .setSpecFormat("SpecFormat")
                                                .setSpecMeasure("SpecMeasure")
                                                .build(),
                                        RecordDataRequirementsSpecifications.newBuilder()
                                                .setSpecCode(1184)
                                                .setSpecName("Технологические факторы")
                                                .setSpecTypeCode(1)
                                                .setSpecTypeValue(2)
                                                .setSpecTypeName("Строка")
                                                .setListValues(List.of(
                                                        RecordDataRequirementsSpecificationsListValues.newBuilder()
                                                                .setValue("U05")
                                                                .build(),
                                                        RecordDataRequirementsSpecificationsListValues.newBuilder()
                                                                .setValue("U05")
                                                                .setDescription("Ушко")
                                                                .build()
                                                ))
                                                .setSpecDecryption("SpecDecryption")
                                                .setSpecFormat("SpecFormat")
                                                .setSpecMeasure("SpecMeasure")
                                                .build()
                                ))
                                .setChemicalReq(List.of(
                                        RecordChemicalReq.newBuilder()
                                                .setChemCode(9001)
                                                .setChemName("Массовая доля водорода, H")
                                                .setValueMax(0.1)
                                                .setValueMin(0.05)
                                                .setDigitsQuantity(2)
                                                .build(),
                                        RecordChemicalReq.newBuilder()
                                                .setChemCode(9005)
                                                .setChemName("Массовая доля бора, B")
                                                .setValueMax(0.01)
                                                .setValueMin(0.005)
                                                .setDigitsQuantity(3)
                                                .build()
                                ))
                                .build())
                        .setSpecifications(List.of(
                                RecordDataSpecifications.newBuilder()
                                        .setSpecCode(40)
                                        .setSpecValue("2.6")
                                        .setSpecName("Замер толщины - середина")
                                        .setSpecTypeCode(2)
                                        .setSpecTypeValue(1)
                                        .setSpecTypeName("Число")
                                        .setSpecDecryption("SpecDecryption")
                                        .setSpecFormat("SpecFormat")
                                        .setSpecMeasure("SpecMeasure")
                                        .build(),
                                RecordDataSpecifications.newBuilder()
                                        .setSpecCode(1184)
                                        .setSpecName("Технологические факторы")
                                        .setSpecTypeCode(1)
                                        .setSpecTypeValue(2)
                                        .setSpecTypeName("Строка")
                                        .setListValues(List.of(
                                                RecordDataSpecificationsListValues.newBuilder()
                                                        .setValue("U05")
                                                        .build(),
                                                RecordDataSpecificationsListValues.newBuilder()
                                                        .setValue("U05")
                                                        .setDescription("Ушко")
                                                        .build()
                                        ))
                                        .setSpecDecryption("SpecDecryption")
                                        .setSpecFormat("SpecFormat")
                                        .setSpecMeasure("SpecMeasure")
                                        .build()
                        ))
                        .setChemData(List.of(
                                RecordChemData.newBuilder()
                                        .setChemical(List.of(
                                                RecordChemical.newBuilder()
                                                        .setChemCode(9001)
                                                        .setChemName("Массовая доля водорода, H")
                                                        .setChemValue(0.07)
                                                        .build(),
                                                RecordChemical.newBuilder()
                                                        .setChemCode(9005)
                                                        .setChemName("Массовая доля бора, B")
                                                        .setChemValue(0.007)
                                                        .build()
                                        ))
                                        .setSamplingPlaceName("УНРС-11")
                                        .setSampleNum(11)
                                        .setAnalysisCode("12")
                                        .setHeat(1)
                                        .setProbeCode("К")
                                        .setSampleId(13L)
                                        .setReason("reason")
                                        .build()
                        ))
                        .build())
                .build();

        var restRequest = CcmKc2Request.builder()
                .ts("2023-04-03T06:59:12.426-03:00")
                .pk(Pk.builder()
                        .systemCode("7")
                        .id("12345")
                        .build())
                .data(Record.builder()
                        .primeId("12345")
                        .werks(1L)
                        .werksName("werksName-1")
                        .workshop(7)
                        .workshopName("КЦ2")
                        .orderNum(123L)
                        .orderPos(1)
                        .unitCode("11")
                        .unitName("УНРС-11")
                        .weightNet(BigDecimal.valueOf(4.16))
                        .marking(Marking.builder()
                                .heat(1)
                                .strand(2)
                                .slab(3)
                                .build())
                        .markingAcc(Marking.builder()
                                .heat(1)
                                .strand(2)
                                .slab(3)
                                .build())
                        .requirements(Requirements.builder()
                                .planTask(PlanTask.builder()
                                        .planTaskId(1)
                                        .planTaskLineId(2)
                                        .build())
                                .specifications(List.of(
                                        Specification.builder()
                                                .specCode(40)
                                                .specValue("2.5..2.7")
                                                .specName("Замер толщины - середина")
                                                .specTypeCode(SpecTypeCode.NUMBER)
                                                .specTypeValue(SpecTypeValue.SIMPLE)
                                                .specTypeName("Число")
                                                .specDecryption("SpecDecryption")
                                                .specFormat("SpecFormat")
                                                .specMeasure("SpecMeasure")
                                                .build(),
                                        Specification.builder()
                                                .specCode(1184)
                                                .specName("Технологические факторы")
                                                .specTypeCode(SpecTypeCode.STRING)
                                                .specTypeValue(SpecTypeValue.ENUMERABLE)
                                                .specTypeName("Строка")
                                                .listValues(List.of(
                                                        SpecValue.builder()
                                                                .value("U05")
                                                                .build(),
                                                        SpecValue.builder()
                                                                .value("U05")
                                                                .description("Ушко")
                                                                .build()
                                                ))
                                                .specDecryption("SpecDecryption")
                                                .specFormat("SpecFormat")
                                                .specMeasure("SpecMeasure")
                                                .build()
                                ))
                                .chemicalReq(List.of(
                                        ChemicalReq.builder()
                                                .chemCode(9001)
                                                .chemName("Массовая доля водорода, H")
                                                .valueMax(BigDecimal.valueOf(0.1))
                                                .valueMin(BigDecimal.valueOf(0.05))
                                                .digitsQuantity(2)
                                                .build(),
                                        ChemicalReq.builder()
                                                .chemCode(9005)
                                                .chemName("Массовая доля бора, B")
                                                .valueMax(BigDecimal.valueOf(0.01))
                                                .valueMin(BigDecimal.valueOf(0.005))
                                                .digitsQuantity(3)
                                                .build()
                                ))
                                .build())
                        .specifications(List.of(
                                Specification.builder()
                                        .specCode(40)
                                        .specValue("2.6")
                                        .specName("Замер толщины - середина")
                                        .specTypeCode(SpecTypeCode.NUMBER)
                                        .specTypeValue(SpecTypeValue.SIMPLE)
                                        .specTypeName("Число")
                                        .specDecryption("SpecDecryption")
                                        .specFormat("SpecFormat")
                                        .specMeasure("SpecMeasure")
                                        .build(),
                                Specification.builder()
                                        .specCode(1184)
                                        .specName("Технологические факторы")
                                        .specTypeCode(SpecTypeCode.STRING)
                                        .specTypeValue(SpecTypeValue.ENUMERABLE)
                                        .specTypeName("Строка")
                                        .listValues(List.of(
                                                SpecValue.builder()
                                                        .value("U05")
                                                        .build(),
                                                SpecValue.builder()
                                                        .value("U05")
                                                        .description("Ушко")
                                                        .build()
                                        ))
                                        .specDecryption("SpecDecryption")
                                        .specFormat("SpecFormat")
                                        .specMeasure("SpecMeasure")
                                        .build()
                        ))
                        .chemData(List.of(
                                ChemData.builder()
                                        .chemical(List.of(
                                                Chemical.builder()
                                                        .chemCode(9001)
                                                        .chemName("Массовая доля водорода, H")
                                                        .chemValue(BigDecimal.valueOf(0.07))
                                                        .build(),
                                                Chemical.builder()
                                                        .chemCode(9005)
                                                        .chemName("Массовая доля бора, B")
                                                        .chemValue(BigDecimal.valueOf(0.007))
                                                        .build()
                                        ))
                                        .samplingPlaceName("УНРС-11")
                                        .sampleNum(11)
                                        .analysisCode("12")
                                        .heat(1)
                                        .probeCode("К")
                                        .sampleId(13L)
                                        .reason("reason")
                                        .build()
                        ))
                        .build())
                .build();

        var kafkaProcessed = kafkaRequestAdapter.adapt(kafkaRequest);
        var restProcessed = restRequestAdapter.adapt(restRequest);

        Assertions.assertEquals(kafkaProcessed, restProcessed);

    }


}
