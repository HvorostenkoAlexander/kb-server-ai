package com.nlmk.kb.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.attestation.product.api.pam.AnalysisValue;
import com.nlmk.attestation.product.api.specification.TypeCode;
import com.nlmk.kb.server.api.ccm.pts.CcmPtsRequest;
import com.nlmk.kb.server.service.AttestationMessageService;
import nlmk.EnumOp;
import nlmk.nlmk.l3.ccm.pts.DbAttestationRequestVer1;
import nlmk.nlmk.l3.ccm.pts.db.attestation.request.ver1.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AttestationController.class)
class AttestationControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private AttestationMessageService attestationMessageService;
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void postAttestationCcmPts() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/attestation/ccm/pts")
                        .header(HttpHeaders.AUTHORIZATION, "T V")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        mvc.perform(MockMvcRequestBuilders.post("/attestation/ccm/pts")
                        .header(HttpHeaders.AUTHORIZATION, "T V")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(prepareMinimalRequest()))
                .andExpect(status().isOk());

        mvc.perform(MockMvcRequestBuilders.post("/attestation/ccm/pts")
                        .header(HttpHeaders.AUTHORIZATION, "T V")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(prepareMinimalRequestData()))
                .andExpect(status().isOk());

        // согласованный вариант REST = AVRO
        mvc.perform(MockMvcRequestBuilders.post("/attestation/ccm/pts")
                        .header(HttpHeaders.AUTHORIZATION, "T V")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(prepareMinimalRequestDataByAVRO()))
                .andExpect(status().isOk());
    }

    private String prepareMinimalRequest() throws Exception {
        return mapper.writeValueAsString(
                CcmPtsRequest.builder()
                        .ts("2022-07-01T00:00:00.000Z")
                        .pk(CcmPtsRequest.Pk.builder().systemCode("sc").id("id").build())
                        .build()
        );
    }

    private String prepareMinimalRequestData() throws Exception {
        return mapper.writeValueAsString(
                CcmPtsRequest.builder()
                        .ts("2022-07-01T00:00:00.000Z")
                        .pk(CcmPtsRequest.Pk.builder().systemCode("sc").id("id").build())
                        .data(CcmPtsRequest.Record.builder()
                                .werks(10).werksName("w10")
                                .kceh(11).kcehName("k11")
                                .unitCode(12).unitName("u12")
                                .storageCode(13).storageName("s13")
                                .marking(CcmPtsRequest.Marking.builder().nplv(20).hnum(21).tnum(22).roll(23).build())
                                .weightNet(14.0)
                                .geometry(CcmPtsRequest.Geometry.builder().thickness(30.0).width(32.0).build())
                                .bundles(List.of(
                                        CcmPtsRequest.Bundle.builder().stripId(40L).stripNum(41)
                                                .stripWidth(42.0).stripWeight(43.0).build()
                                ))
                                .specifications(List.of(
                                        CcmPtsRequest.Specification.builder()
                                                .specCode(50).specName("s51")
                                                .specTypeCode(52).specTypeName("s53")
                                                .specTypeValue(CcmPtsRequest.SpecTypeValue.SIMPLE)
                                                .listValues(List.of(
                                                        CcmPtsRequest.OneSpecValue.builder().value("v54").build()
                                                )).build()
                                ))
                                .properties(List.of(
                                        CcmPtsRequest.OneProperty.builder()
                                                .typeCode(60).typeName("t61").testDate("2022-09-16T14:22:33+03:00").probeCode(70).probeName("p70")
                                                .analyzes(List.of(
                                                        CcmPtsRequest.OnePropAnalyze.builder()
                                                                .samplingPlaceCode(62).samplingPlaceName("s63")
                                                                .analysisValue(AnalysisValue.BEST)
                                                                .listValues(List.of(
                                                                        CcmPtsRequest.OneAnalyzeValue.builder()
                                                                                .attrCode(64)
                                                                                .attrType(TypeCode.NUMBER).build()
                                                                )).build()
                                                ))
                                                .attestationList(List.of(
                                                        CcmPtsRequest.OnePropAtt.builder()
                                                                .typeCode(70).typeName("t71")
                                                                .listValues(List.of(
                                                                        CcmPtsRequest.OneAttValue.builder()
                                                                                .side(CcmPtsRequest.Side.BACK)
                                                                                .attrCode(73).attrValue(74.0)
                                                                                .build()
                                                                )).build()
                                                ))
                                                .listValues(List.of(
                                                        CcmPtsRequest.OnePropValue.builder()
                                                                .attrCode(80).attrType(TypeCode.STRING)
                                                                .attrValue(List.of(
                                                                        CcmPtsRequest.OnePropValueAttr.builder()
                                                                                .value("v80").build()
                                                                )).build()
                                                ))
                                                .build()
                                ))
                                .chemical(List.of(
                                        CcmPtsRequest.Chemical.builder()
                                                .id(90).listValues(List.of(
                                                        CcmPtsRequest.OneChemicalValue.builder()
                                                                .code(91).name("n92").build()
                                                )).build()
                                ))
                                .build())
                        .build()
        );
    }

    private String prepareMinimalRequestDataByAVRO() throws Exception {
        return DbAttestationRequestVer1.newBuilder()
                .setTs("2022-09-02T14:36:25.000+05:00")
                .setOp(EnumOp.U)
                .setPk(PkType.newBuilder().setId("42").setSystemCode("16").build())
                .setData(RecordData.newBuilder()
                        .setWerks(1).setWerksName("1")
                        .setKceh(11).setKcehName("11")
                        .setUnitCode(2).setUnitName("2")
                        .setStorageCode(3).setStorageName("3")
                        .setWeightNet(10.86f)
                        .setMarking(RecordMarking.newBuilder().setNplv(2106684).setHnum(25217).setTnum(1).setRoll(1).build())
                        .setGeometry(RecordGeometry.newBuilder().setThickness(2.65f).setWidth(1232.0f).build())
                        .setSpecifications(List.of(
                                RecordSpecifications.newBuilder().setSpecCode(1).setSpecName("1")
                                        .setSpecTypeCode(2).setSpecTypeName("2")
                                        .setSpecTypeValue(CcmPtsRequest.SpecTypeValue.SIMPLE.getValue())
                                        .setListValues(List.of(
                                                RecordDataSpecificationsListValues.newBuilder().setValue("vSpec").build()
                                        )).build()
                        ))
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
                                        .setTypeCode(3).setTypeName("3")
                                        .setTestDate("2022-01-01")
                                        .setProbeCode(3).setProbeName("3")
                                        .setAnalyzes(List.of(
                                                RecordAnalyzes.newBuilder()
                                                        .setSamplingPlaceCode(3).setSamplingPlaceName("s3")
                                                        .setAnalysisValue(AnalysisValue.BEST.getValue())
                                                        .setListValues(List.of(
                                                                RecordDataPropertiesAnalyzesListValues.newBuilder()
                                                                        .setAttrCode(31).setAttrType(1)
                                                                        .setAttrValue("31").build()
                                                        ))
                                                        .build()
                                        ))
                                        .setAttestationList(List.of(
                                                RecordAttestationList.newBuilder()
                                                        .setTypeCode(40).setTypeName("t40")
                                                        .setListValues(List.of(
                                                                RecordDataPropertiesAttestationListListValues.newBuilder()
                                                                        .setAttrCode(41).setAttrValue(4.1f)
                                                                        .setSide(CcmPtsRequest.Side.BACK.getValue())
                                                                        .build()
                                                        )).build()
                                        ))
                                        .setListValues(List.of(
                                                RecordDataPropertiesListValues.newBuilder()
                                                        .setAttrCode(3).setAttrType(1)
                                                        .setAttrValue(List.of(
                                                                RecordDataPropertiesListValuesAttrValue.newBuilder().setValue("3").build()
                                                        )).build()
                                        ))
                                        .build()
                        ))
                        .setChemical(List.of(
                                RecordChemical.newBuilder().setId(10).setListValues(List.of(
                                        RecordDataChemicalListValues.newBuilder()
                                                .setCode(11).setValue(1.2f).setName("v11").build()
                                )).build()
                        ))
                        .build())
                .build().toString();
    }

}
