package com.nlmk.kb.server.controller;

import com.nlmk.attestation.product.api.pam.AttestationRequest;
import com.nlmk.attestation.product.api.pam.Value;
import com.nlmk.attestation.zorder.ZORDERS051;
import com.nlmk.attestation.zorder.ZORDERS051E1EDK01;
import com.nlmk.attestation.zorder.ZORDRSPORDERS05ZORDERS051;
import com.nlmk.kb.server.entity.CcmMessage;
import com.nlmk.kb.server.exception.*;
import com.nlmk.kb.server.service.AttestationMessageService;
import com.nlmk.kb.server.service.ccm.CcmCommonService;
import com.nlmk.kb.server.service.ccm.CcmMessageService;
import com.nlmk.kb.server.service.pdm.PdmMessageService;
import com.nlmk.kb.server.service.sap.S3Service;
import com.nlmk.kb.server.service.sender.ProductSender;
import com.nlmk.kb.server.service.sender.PsmSender;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(KbController.class)
class KbControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private S3Service s3Service;
    @MockBean
    private CcmMessageService ccmMessageService;
    @MockBean
    private PdmMessageService pdmMessageService;
    @MockBean
    private CcmCommonService ccmCommonService;
    @MockBean
    private PsmSender psmSender;
    @MockBean
    private ProductSender productSender;
    @MockBean
    private AttestationMessageService attestationMessageService;

    @Test
    void sendingSapMessage() throws Exception {
        var E1EDK01 = new ZORDERS051E1EDK01();
        E1EDK01.setBELNR("12345");
        var idoc = new ZORDRSPORDERS05ZORDERS051();
        idoc.setE1EDK01(E1EDK01);
        ZORDERS051 zorder = new ZORDERS051();
        zorder.setIDOC(idoc);

        when(s3Service.getZorder(any(String.class))).thenThrow(new S3ClientException("---"));

        mvc.perform(MockMvcRequestBuilders.post("/sap_message/zorder")
                        .header(HttpHeaders.AUTHORIZATION, "T V")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("zorder xml"))
                .andExpect(status().isBadRequest());

        when(s3Service.getZorder(any(String.class))).thenReturn(zorder);

        when(psmSender.postZorder(any(ZORDERS051.class))).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        mvc.perform(MockMvcRequestBuilders.post("/sap_message/zorder")
                        .header(HttpHeaders.AUTHORIZATION, "T V")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("zorder xml"))
                .andExpect(status().isBadRequest());

        Mockito.when(psmSender.postZorder(any(ZORDERS051.class))).thenReturn(10);

        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post("/sap_message/zorder")
                        .header(HttpHeaders.AUTHORIZATION, "T V")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("zorder xml"))
                .andExpect(status().isOk());

        MvcResult result = resultActions.andReturn();
        String contentAsString = result.getResponse().getContentAsString();

        Assertions.assertEquals("BELNR: 12345", contentAsString);
    }

    @Test
    void sendAttestationResult() throws Exception {
        final var url = "/send_attestation_result";

        mvc.perform(MockMvcRequestBuilders.post(url)
                        .header(HttpHeaders.AUTHORIZATION, "T V")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());

        mvc.perform(MockMvcRequestBuilders.post(url)
                        .header(HttpHeaders.AUTHORIZATION, "T V")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        doThrow(ProductSenderException.class).when(productSender).send(any());
        mvc.perform(MockMvcRequestBuilders.post(url)
                        .header(HttpHeaders.AUTHORIZATION, "T V")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isInternalServerError());

        doThrow(KafkaRestConfigException.class).when(productSender).send(any());
        mvc.perform(MockMvcRequestBuilders.post(url)
                        .header(HttpHeaders.AUTHORIZATION, "T V")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isServiceUnavailable());

        doThrow(KafkaRestException.class).when(productSender).send(any());
        mvc.perform(MockMvcRequestBuilders.post(url)
                        .header(HttpHeaders.AUTHORIZATION, "T V")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadGateway());
    }

    @Test
    void getAttestationRequestForPrimeId() throws Exception {
        final var url = "/attestation/request/pi100";

        when(ccmMessageService.findByPrimeId(any())).thenReturn(List.of());
        when(attestationMessageService.findAllAttestationRequestByPrimeId(any())).thenReturn(List.of());

        mvc.perform(MockMvcRequestBuilders.get(url)
                        .header(HttpHeaders.AUTHORIZATION, "T V"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[]"));

        when(ccmMessageService.findByPrimeId(any())).thenReturn(List.of(
                CcmMessage.builder().request(
                        AttestationRequest.builder().value(Value.builder().build()).build()
                ).build()
        ));

        mvc.perform(MockMvcRequestBuilders.get(url)
                        .header(HttpHeaders.AUTHORIZATION, "T V"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)));

        when(attestationMessageService.findAllAttestationRequestByPrimeId(any())).thenReturn(List.of(
                AttestationRequest.builder().value(Value.builder().build()).build()
        ));

        mvc.perform(MockMvcRequestBuilders.get(url)
                        .header(HttpHeaders.AUTHORIZATION, "T V"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));

        doThrow(CcmRequestParsingException.class).when(attestationMessageService)
                .findAllAttestationRequestByPrimeId(any());

        mvc.perform(MockMvcRequestBuilders.get(url)
                        .header(HttpHeaders.AUTHORIZATION, "T V"))
                .andExpect(status().isBadRequest());
    }

}
