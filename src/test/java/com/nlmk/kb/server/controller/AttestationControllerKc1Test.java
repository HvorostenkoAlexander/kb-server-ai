package com.nlmk.kb.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.attestation.product.api.specification.SpecCode;
import com.nlmk.kb.server.api.ccm.kc.CcmKc1Request;
import com.nlmk.kb.server.service.AttestationMessageService;
import java.math.BigDecimal;
import java.util.List;
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

    @Autowired
    private MockMvc mvc;
    @MockBean
    private AttestationMessageService attestationMessageService;
    private final ObjectMapper mapper = new ObjectMapper();

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
        return mapper.writeValueAsString(
                CcmKc1Request.builder()
                        .ts("2023-03-13T00:00:00.000Z")
                        .pk(CcmKc1Request.Pk.builder().systemCode("sc").id("id").build())
                        .data(CcmKc1Request.Record.builder()
                                .werks(10L).werksName("w10")
                                .kceh(11).kcehName("k11")
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
                                                        .specTypeCode(CcmKc1Request.SpecTypeCode.NUMBER)
                                                        .specTypeName("s53")
                                                        .specTypeValue(CcmKc1Request.SpecTypeValue.SIMPLE)
                                                        .listValues(List.of(
                                                                CcmKc1Request.SpecValue.builder().value("v54").description("v55").build()
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
                                                .specTypeCode(CcmKc1Request.SpecTypeCode.NUMBER)
                                                .specTypeName("s53")
                                                .specTypeValue(CcmKc1Request.SpecTypeValue.SIMPLE)
                                                .listValues(List.of(
                                                        CcmKc1Request.SpecValue.builder().value("v54").description("v55").build()
                                                ))
                                                .specDecryption("s15")
                                                .build()
                                ))
                                .build())
                        .build()
        );
    }

}
