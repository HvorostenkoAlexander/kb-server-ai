package com.nlmk.kb.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.attestation.product.api.pam.AnalysisValue;
import com.nlmk.attestation.product.api.specification.TypeCode;
import com.nlmk.kb.server.api.ccm.pts.CcmPtsRequest;
import com.nlmk.kb.server.service.AttestationMessageService;
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
                .andExpect(status().isCreated());

        mvc.perform(MockMvcRequestBuilders.post("/attestation/ccm/pts")
                        .header(HttpHeaders.AUTHORIZATION, "T V")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(prepareRequestWithMinimalData()))
                .andExpect(status().isCreated());
    }

    private String prepareMinimalRequest() throws Exception {
        return mapper.writeValueAsString(
                CcmPtsRequest.builder()
                        .ts("2022-07-01T00:00:00.000Z")
                        .pk(CcmPtsRequest.Pk.builder().systemCode("sc").id("id").build())
                        .build()
        );
    }

    private String prepareRequestWithMinimalData() throws Exception {
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
                                                )).build()
                                ))
                                .chemical(List.of(
                                        CcmPtsRequest.Chemical.builder()
                                                .id(80).listValues(List.of(
                                                        CcmPtsRequest.OneChemicalValue.builder()
                                                                .code(81).name("n82").build()
                                                )).build()
                                ))
                                .build())
                        .build()
        );
    }

}
