package com.nlmk.kb.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.attestation.product.api.Kceh;
import com.nlmk.attestation.product.api.ProductDto;
import com.nlmk.attestation.product.api.pam.AttestationRequest;
import com.nlmk.attestation.product.api.pam.ProductAttestationResultDto;
import com.nlmk.attestation.product.api.pam.Value;
import com.nlmk.attestation.zorder.ZORDERS051;
import com.nlmk.attestation.zorder.ZORDERS051E1EDK01;
import com.nlmk.attestation.zorder.ZORDRSPORDERS05ZORDERS051;
import com.nlmk.kb.server.entity.AttestationMessage;
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

import java.util.Date;
import java.util.Optional;

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

    private final ObjectMapper objectMapper = new ObjectMapper();

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
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mvc.perform(MockMvcRequestBuilders.post(url)
                        .header(HttpHeaders.AUTHORIZATION, "T V")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        mvc.perform(MockMvcRequestBuilders.post(url)
                        .header(HttpHeaders.AUTHORIZATION, "T V")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ProductAttestationResultDto.builder().build())))
                .andExpect(status().isBadRequest());

        final var content = objectMapper.writeValueAsString(
                ProductAttestationResultDto.builder()
                        .result(ProductDto.builder().build())
                        .kceh(Kceh.PGP)
                        .build()
        );

        mvc.perform(MockMvcRequestBuilders.post(url)
                        .header(HttpHeaders.AUTHORIZATION, "T V")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk());

        doThrow(ProductSenderException.class).when(productSender).send(any(), any());
        mvc.perform(MockMvcRequestBuilders.post(url)
                        .header(HttpHeaders.AUTHORIZATION, "T V")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isInternalServerError());

        doThrow(KafkaRestConfigException.class).when(productSender).send(any(), any());
        mvc.perform(MockMvcRequestBuilders.post(url)
                        .header(HttpHeaders.AUTHORIZATION, "T V")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isServiceUnavailable());

        doThrow(KafkaRestException.class).when(productSender).send(any(), any());
        mvc.perform(MockMvcRequestBuilders.post(url)
                        .header(HttpHeaders.AUTHORIZATION, "T V")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadGateway());
    }

    @Test
    void getAttestationRequestForPrimeId() throws Exception {
        final var url = "/attestation/request/pi100";

        when(ccmMessageService.findLastMessage(any())).thenReturn(Optional.empty());
        when(attestationMessageService.findLastAttestationMessage(any())).thenReturn(Optional.empty());

        mvc.perform(MockMvcRequestBuilders.get(url)
                        .header(HttpHeaders.AUTHORIZATION, "T V"))
                .andExpect(status().isNotFound());

        when(ccmMessageService.findLastMessage(any())).thenReturn(Optional.of(
                CcmMessage.builder().request(
                        AttestationRequest.builder().value(Value.builder().build()).build()
                ).kbReceiptTs(new Date(1_000_000_000L)).build()
        ));
        mvc.perform(MockMvcRequestBuilders.get(url)
                        .header(HttpHeaders.AUTHORIZATION, "T V"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());

        // выбор одного из двух
        when(attestationMessageService.findLastAttestationMessage(any())).thenReturn(Optional.of(
                AttestationMessage.builder().request("{}")
                        .receiptTs(new Date(1_200_000_000L)).build()
        ));
        when(attestationMessageService.getAttestationRequestFromMessage(any()))
                .thenReturn(AttestationRequest.builder().value(Value.builder().build()).build());
        mvc.perform(MockMvcRequestBuilders.get(url)
                        .header(HttpHeaders.AUTHORIZATION, "T V"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());

        doThrow(CcmRequestParsingException.class).when(attestationMessageService)
                .getAttestationRequestFromMessage(any());

        mvc.perform(MockMvcRequestBuilders.get(url)
                        .header(HttpHeaders.AUTHORIZATION, "T V"))
                .andExpect(status().isBadRequest());
    }

}
