package com.nlmk.kb.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.kb.server.api.CcmPtsRequest;
import com.nlmk.kb.server.service.AttestationMessageService;
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
                        .content(mapper.writeValueAsString(
                                CcmPtsRequest.builder().ts("2022-07-01T00:00:00.000Z").build()
                        )))
                .andExpect(status().isCreated());
    }

}
