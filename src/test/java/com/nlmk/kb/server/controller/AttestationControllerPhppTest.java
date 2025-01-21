package com.nlmk.kb.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.attestation.product.api.Kceh;
import com.nlmk.kb.server.api.ccm.SpecTypeCode;
import com.nlmk.kb.server.api.ccm.SpecTypeValue;
import com.nlmk.kb.server.api.ccm.phpp.CcmPhppRequest;
import com.nlmk.kb.server.service.AttestationMessageService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AttestationController.class)
public class AttestationControllerPhppTest {

    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private MockMvc mvc;
    @MockBean
    private AttestationMessageService attestationMessageService;

    @Test
    void postAttestationCcmPhpp() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/attestation/ccm/phpp")
                        .header(HttpHeaders.AUTHORIZATION, "T V")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        mvc.perform(MockMvcRequestBuilders.post("/attestation/ccm/phpp")
                        .header(HttpHeaders.AUTHORIZATION, "T V")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(prepareMinimalRequest()))
                .andExpect(status().isOk());

        mvc.perform(MockMvcRequestBuilders.post("/attestation/ccm/phpp")
                        .header(HttpHeaders.AUTHORIZATION, "T V")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(prepareMinimalRequestData()))
                .andExpect(status().isOk());

    }

    private String prepareMinimalRequest() throws Exception {
        return mapper.writeValueAsString(
                CcmPhppRequest.builder()
                        .ts("2025-02-20T00:00:00.000Z")
                        .pk(CcmPhppRequest.Pk.builder().systemCode("sc").id("id").build())
                        .build()
        );
    }

    private String prepareMinimalRequestData() throws Exception {
        return mapper.writeValueAsString(CcmPhppRequest.builder()
                .ts("2023-03-13T00:00:00.000Z")
                .pk(CcmPhppRequest.Pk.builder().systemCode("sc").id("id").build())
                .data(CcmPhppRequest.Record.builder()
                        .roll(111)
                        .thickness(BigDecimal.valueOf(100))
                        .width(BigDecimal.valueOf(200))
                        .weightNet(BigDecimal.valueOf(30))
                        .workshopNum(Kceh.PHPP.getValue())
                        .specifications(List.of(
                                CcmPhppRequest.Specification.builder()
                                        .specCode(50).specName("s51")
                                        .specTypeCode(SpecTypeCode.NUMBER.getValue())
                                        .specTypeName("s53")
                                        .specTypeValue(SpecTypeValue.SIMPLE)
                                        .listValues(List.of(
                                                CcmPhppRequest.OneSpecValue.builder().value("v54")
                                                        .build()
                                        ))
                                        .build()
                        ))
                        .build())
                .build());
    }

}
