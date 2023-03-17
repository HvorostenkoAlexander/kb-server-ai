package com.nlmk.kb.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.attestation.product.api.specification.SpecCode;
import com.nlmk.kb.server.api.ccm.SpecTypeCode;
import com.nlmk.kb.server.api.ccm.SpecTypeValue;
import com.nlmk.kb.server.api.ccm.kc.CcmKc1Request;
import com.nlmk.kb.server.service.AttestationMessageService;
import java.math.BigDecimal;
import java.util.List;
import nlmk.EnumOp;
import nlmk.nlmk.l3.sus.kc1.DbAttestRequestVer0;
import nlmk.nlmk.l3.sus.kc1.db.attestrequest.ver0.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AttestationController.class)
class AttestationControllerKc1Test {

    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private MockMvc mvc;
    @MockBean
    private AttestationMessageService attestationMessageService;

    @Test
    void postAttestationCcmKc() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/attestation/ccm/kc1")
                        .header(HttpHeaders.AUTHORIZATION, "T V")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        mvc.perform(MockMvcRequestBuilders.post("/attestation/ccm/kc1")
                        .header(HttpHeaders.AUTHORIZATION, "T V")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(prepareMinimalRequest()))
                .andExpect(status().isOk());

        mvc.perform(MockMvcRequestBuilders.post("/attestation/ccm/kc1")
                        .header(HttpHeaders.AUTHORIZATION, "T V")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(prepareMinimalRequestData()))
                .andExpect(status().isOk());

        // согласованный вариант REST = AVRO
        mvc.perform(MockMvcRequestBuilders.post("/attestation/ccm/kc1")
                        .header(HttpHeaders.AUTHORIZATION, "T V")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(prepareMinimalRequestDataByAVRO()))
                .andExpect(status().isOk());
    }

    private String prepareMinimalRequest() throws Exception {
        return mapper.writeValueAsString(
                CcmKc1Request.builder()
                        .ts("2023-03-13T00:00:00.000Z")
                        .pk(CcmKc1Request.Pk.builder().systemCode("sc").id("id").build())
                        .build()
        );
    }

    private String prepareMinimalRequestData() throws Exception {
        return mapper.writeValueAsString(CcmKc1Request.builder()
                .ts("2023-03-13T00:00:00.000Z")
                .pk(CcmKc1Request.Pk.builder().systemCode("sc").id("id").build())
                .data(CcmKc1Request.Record.builder()
                        .primeId("id")
                        .werks(10L).werksName("w10")
                        .kceh(4).kcehName("КЦ1")
                        .unitCode("УНРС12").unitName("u12")
                        .marking(CcmKc1Request.Marking.builder().heat(13).strand(13).slab(13).build())
                        .markingAcc(CcmKc1Request.Marking.builder().heat(13).strand(13).slab(13).build())
                        .weightNet(BigDecimal.valueOf(14.0))
                        .requirements(CcmKc1Request.Requirements.builder()
                                .planTask(CcmKc1Request.PlanTask.builder().planTaskId(15).planTaskLineId(1).build())
                                .chemicalReq(List.of(
                                        CcmKc1Request.ChemicalReq.builder()
                                                .chemCode(SpecCode.MASS_FRACTION_N.getValue())
                                                .chemName(SpecCode.MASS_FRACTION_N.getDesc())
                                                .valueMin(BigDecimal.valueOf(0.001))
                                                .valueMax(BigDecimal.valueOf(0.003))
                                                .digitsQuantity(3)
                                                .build()
                                ))
                                .specifications(List.of(
                                        CcmKc1Request.Specification.builder()
                                                .specCode(50).specName("s51")
                                                .specTypeCode(SpecTypeCode.NUMBER)
                                                .specTypeName("s53")
                                                .specTypeValue(SpecTypeValue.SIMPLE)
                                                .listValues(List.of(
                                                        CcmKc1Request.SpecValue.builder().value("v54")
                                                                .description("v55").build()
                                                ))
                                                .specDecryption("s15")
                                                .build()
                                ))
                                .build())
                        .chemData(List.of(
                                CcmKc1Request.ChemData.builder()
                                        .sampleId(1L)
                                        .probeCode("К")
                                        .analysisCode("aC")
                                        .sampleNum(3)
                                        .heat(1)
                                        .samplingPlaceName("sP")
                                        .reason("r")
                                        .chemical(List.of(
                                                CcmKc1Request.Chemical.builder()
                                                        .chemCode(SpecCode.MASS_FRACTION_N.getValue())
                                                        .chemName(SpecCode.MASS_FRACTION_N.getDesc())
                                                        .chemValue(BigDecimal.valueOf(0.002))
                                                        .build()
                                        ))
                                        .build())
                        )
                        .specifications(List.of(
                                CcmKc1Request.Specification.builder()
                                        .specCode(50).specName("s51")
                                        .specTypeCode(SpecTypeCode.NUMBER)
                                        .specTypeName("s53")
                                        .specTypeValue(SpecTypeValue.SIMPLE)
                                        .listValues(List.of(
                                                CcmKc1Request.SpecValue.builder().value("v54").description("v55")
                                                        .build()
                                        ))
                                        .specDecryption("s15")
                                        .build()
                        ))
                        .build())
                .build());
    }

    private String prepareMinimalRequestDataByAVRO() {
        return DbAttestRequestVer0.newBuilder()
                .setTs("2023-03-13T00:00:00.000Z")
                .setOp(EnumOp.U)
                .setPk(PkType.newBuilder().setId("42").setSystemCode("16").build())
                .setData(RecordData.newBuilder()
                        .setPrimeId("id")
                        .setWerks(10).setWerksName("10L")
                        .setWorkshop(4).setWorkshopName("КЦ1")
                        .setUnitCode("УНРС12")
                        .setUnitName("u12")
                        .setMarking(RecordMarking.newBuilder().setHeat(13).setStrand(13).setSlab(13).build())
                        .setMarkingAcc(RecordMarkingAcc.newBuilder().setHeat(13).setStrand(13).setSlab(13).build())
                        .setWeightNet(14.0f)
                        .setRequirements(RecordRequirements.newBuilder()
                                .setPlanTask(RecordPlanTask.newBuilder()
                                        .setPlanTaskId(1)
                                        .setPlanTaskLineId(2)
                                        .build())
                                .setChemicalReq(List.of(
                                        RecordChemicalReq.newBuilder()
                                                .setChemCode(SpecCode.MASS_FRACTION_N.getValue())
                                                .setChemName(SpecCode.MASS_FRACTION_N.getDesc())
                                                .setValueMin(0.001)
                                                .setValueMax(0.003)
                                                .setDigitsQuantity(3)
                                                .build()
                                ))
                                .setSpecifications(List.of(
                                        RecordDataRequirementsSpecifications.newBuilder()
                                                .setSpecCode(50).setSpecName("s51")
                                                .setSpecTypeCode(SpecTypeCode.NUMBER.getValue())
                                                .setSpecTypeName("s53")
                                                .setSpecTypeValue(SpecTypeValue.SIMPLE.getValue())
                                                .setListValues(List.of(
                                                        RecordDataRequirementsSpecificationsListValues.newBuilder()
                                                                .setValue("v54")
                                                                .setDescription("v55")
                                                                .build()
                                                ))
                                                .setSpecDecryption("s15")
                                                .build()
                                ))
                                .build())
                        .setChemData(List.of(
                                RecordChemData.newBuilder()
                                        .setSampleId(1L)
                                        .setProbeCode("К")
                                        .setAnalysisCode("aC")
                                        .setSampleNum(3)
                                        .setHeat(1)
                                        .setSamplingPlaceName("sP")
                                        .setReason("reason")
                                        .setChemical(List.of(
                                                RecordChemical.newBuilder()
                                                        .setChemCode(SpecCode.MASS_FRACTION_N.getValue())
                                                        .setChemName(SpecCode.MASS_FRACTION_N.getDesc())
                                                        .setChemValue(0.002)
                                                        .build()
                                        ))
                                        .build()
                        ))
                        .setSpecifications(List.of(
                                RecordDataSpecifications.newBuilder()
                                        .setSpecCode(50).setSpecName("s51")
                                        .setSpecTypeCode(SpecTypeCode.NUMBER.getValue())
                                        .setSpecTypeName("s53")
                                        .setSpecTypeValue(SpecTypeValue.SIMPLE.getValue())
                                        .setListValues(List.of(
                                                RecordDataSpecificationsListValues.newBuilder()
                                                        .setValue("v54").setDescription("v55").build()
                                        ))
                                        .setSpecDecryption("s15")
                                        .build()
                        ))
                        .build())
                .build().toString();
    }


}
