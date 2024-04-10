package com.nlmk.kb.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.attestation.product.api.Kceh;
import com.nlmk.attestation.product.api.ProductDto;
import com.nlmk.attestation.product.api.kb.SapMessageDto;
import com.nlmk.attestation.product.api.pam.ProductAttestationResultDto;
import com.nlmk.attestation.zmmorder.ZMMORDERS05DOP;
import com.nlmk.attestation.zorder.ZORDERS051;
import com.nlmk.attestation.zorder.ZORDERS051E1EDK01;
import com.nlmk.attestation.zorder.ZORDRSPORDERS05ZORDERS051;
import com.nlmk.kb.server.api.CcmMessageSourceDto;
import com.nlmk.kb.server.exception.AttestationResultSenderException;
import com.nlmk.kb.server.exception.DataNotFoundException;
import com.nlmk.kb.server.exception.KafkaRestConfigException;
import com.nlmk.kb.server.exception.RemoteServiceSenderException;
import com.nlmk.kb.server.exception.S3ClientException;
import com.nlmk.kb.server.service.AttestationMessageService;
import com.nlmk.kb.server.service.ccm.CcmCommonService;
import com.nlmk.kb.server.service.ccm.CcmMessageService;
import com.nlmk.kb.server.service.pdm.PdmMessageService;
import com.nlmk.kb.server.service.result.sending.AttestationResultSender;
import com.nlmk.kb.server.service.sap.S3Service;
import com.nlmk.kb.server.service.sap.SapMessageService;
import com.nlmk.kb.server.service.sender.PsmSender;
import org.junit.jupiter.api.Test;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    private SapMessageService sapMessageService;
    @MockBean
    private PsmSender psmSender;
    @MockBean
    private AttestationResultSender productSender;
    @MockBean
    private AttestationMessageService attestationMessageService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void sendingSapZorderMessageShouldReturnBadRequestIfProvidedInvalidXml() throws Exception {
        // given
        when(s3Service.unmarshalZorder(any(String.class))).thenThrow(new S3ClientException("---"));
        // when
        // then
        mvc.perform(MockMvcRequestBuilders.post("/sap_message/zorder")
                        .header(HttpHeaders.AUTHORIZATION, "T V")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("zorder xml"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void sendingSapZorderMessageShouldReturnBadRequestIfCouldNotSendToPsm() throws Exception {
        // given
        ZORDERS051 zorder = getTestZorder();
        when(s3Service.unmarshalZorder(any(String.class))).thenReturn(zorder);
        when(psmSender.postZorder(any(ZORDERS051.class))).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));
        // when
        // then
        mvc.perform(MockMvcRequestBuilders.post("/sap_message/zorder")
                        .header(HttpHeaders.AUTHORIZATION, "T V")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("zorder xml"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void sendingSapZorderMessageShouldReturnZorderBELNRIfSentToPSMSuccessfully() throws Exception {
        // given
        ZORDERS051 zorder = getTestZorder();
        when(s3Service.unmarshalZorder(any(String.class))).thenReturn(zorder);
        when(psmSender.postZorder(any(ZORDERS051.class))).thenReturn(10);
        // when
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post("/sap_message/zorder")
                        .header(HttpHeaders.AUTHORIZATION, "T V")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("zorder xml"))
                .andExpect(status().isOk());
        // then
        MvcResult result = resultActions.andReturn();
        assertThat(result.getResponse().getContentAsString()).isEqualTo("BELNR: 12345");
    }

    @Test
    void sendingSapZmmorderMessageShouldReturnBadRequestIfProvidedInvalidXml() throws Exception {
        // given
        when(s3Service.unmarshalZmmorder(any(String.class))).thenThrow(new S3ClientException("---"));
        // when
        // then
        mvc.perform(MockMvcRequestBuilders.post("/sap_message/zmmorder")
                        .header(HttpHeaders.AUTHORIZATION, "T V")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("zmmorder xml"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void sendingSapZmmorderMessageShouldReturnBadRequestIfCouldNotSendToPsm() throws Exception {
        // given
        ZMMORDERS05DOP zmmorder = getTestZmmorder();
        when(s3Service.unmarshalZmmorder(any(String.class))).thenReturn(zmmorder);
        when(psmSender.postZmmorder(any(ZMMORDERS05DOP.class))).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));
        // when
        // then
        mvc.perform(MockMvcRequestBuilders.post("/sap_message/zmmorder")
                        .header(HttpHeaders.AUTHORIZATION, "T V")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("zmmorder xml"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void sendingSapZmmorderMessageShouldReturnZmmorderBELNRIfSentToPSMSuccessfully() throws Exception {
        // given
        ZMMORDERS05DOP zmmorder = getTestZmmorder();
        when(s3Service.unmarshalZmmorder(any(String.class))).thenReturn(zmmorder);
        when(psmSender.postZmmorder(any(ZMMORDERS05DOP.class))).thenReturn(10);
        // when
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post("/sap_message/zmmorder")
                        .header(HttpHeaders.AUTHORIZATION, "T V")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("zmmorder xml"))
                .andExpect(status().isOk());
        // then
        MvcResult result = resultActions.andReturn();
        assertThat(result.getResponse().getContentAsString()).isEqualTo("BELNR: 12345");
    }

    @Test
    void sendAttestationResultWithoutBodyShouldReturn400() throws Exception {

        mvc.perform(MockMvcRequestBuilders.post("/send_attestation_result")
                        .header(HttpHeaders.AUTHORIZATION, "T V")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void sendAttestationResultWithEmptyBodyShouldReturn400() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/send_attestation_result")
                        .header(HttpHeaders.AUTHORIZATION, "T V")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void sendAttestationResultWithEmptyProductAttestationResultDtoAsBodyShoultReturn400() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/send_attestation_result")
                        .header(HttpHeaders.AUTHORIZATION, "T V")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ProductAttestationResultDto.builder().build())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void canSendAttestationResult() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/send_attestation_result")
                        .header(HttpHeaders.AUTHORIZATION, "T V")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getTestProdAttResDtoJson()))
                .andExpect(status().isOk());
    }

    @Test
    void sendAttestationResultShouldReturn500IfEncounteredAttestationResultSenderExceptionDuringSendingOperation() throws Exception {
        doThrow(AttestationResultSenderException.class).when(productSender).send(any(), any());
        mvc.perform(MockMvcRequestBuilders.post("/send_attestation_result")
                        .header(HttpHeaders.AUTHORIZATION, "T V")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getTestProdAttResDtoJson()))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void sendAttestationResultShouldReturn503IfEncounteredKafkaRestConfigExceptionDuringSendingOperation() throws Exception {
        doThrow(KafkaRestConfigException.class).when(productSender).send(any(), any());
        mvc.perform(MockMvcRequestBuilders.post("/send_attestation_result")
                        .header(HttpHeaders.AUTHORIZATION, "T V")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getTestProdAttResDtoJson()))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    void sendAttestationResultShouldReturn502IfEncounteredRemoteServiceSenderExceptionDuringSendingOperation() throws Exception {
        doThrow(RemoteServiceSenderException.class).when(productSender).send(any(), any());
        mvc.perform(MockMvcRequestBuilders.post("/send_attestation_result")
                        .header(HttpHeaders.AUTHORIZATION, "T V")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getTestProdAttResDtoJson()))
                .andExpect(status().isBadGateway());
    }

    @Test
    void canGetSourceRequestByRequestId() throws Exception {
        // given
        when(ccmMessageService.findSourceMessageByRequestId(any())).thenReturn(Optional.of(
                CcmMessageSourceDto.builder().requestId(1010L).primeId("22")
                        .messageSource(objectMapper.readValue("{}", JsonNode.class))
                        .build()
        ));
        // when
        // then
        mvc.perform(MockMvcRequestBuilders.get("/ccm_source_message?requestId=1010")
                        .header(HttpHeaders.AUTHORIZATION, "T V"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$['requestId']", is(1010)));
    }

    @Test
    void getSourceRequestByRequestIdShouldReturnNullRequestIdIfNotMocked() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/ccm_source_message?requestId=1010")
                        .header(HttpHeaders.AUTHORIZATION, "T V"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$['requestId']", nullValue()));
    }

    @Test
    void canGetSourceRequestByPrimeId() throws Exception {
        // given
        when(ccmMessageService.findSourceMessageByPrimeId(any())).thenReturn(Optional.of(
                CcmMessageSourceDto.builder().requestId(1010L).primeId("22")
                        .messageSource(objectMapper.readValue("{}", JsonNode.class))
                        .build()
        ));
        // when
        // then
        mvc.perform(MockMvcRequestBuilders.get("/attestation/request/22")
                        .header(HttpHeaders.AUTHORIZATION, "T V"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$['requestId']", is(1010)));
    }

    @Test
    void getSourceRequestByPrimeIdShouldReturnNullRequestIdIfNotMocked() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/attestation/request/22")
                        .header(HttpHeaders.AUTHORIZATION, "T V"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$['requestId']", nullValue()));
    }

    @Test
    void getSapMessageNextIdShouldReturn404IfMessageNotFoundById() throws Exception {
        when(sapMessageService.getNextSapMessage(100L))
                .thenThrow(new DataNotFoundException("---"));
        mvc.perform(MockMvcRequestBuilders.get("/sap_message/next?id=100")
                        .header(HttpHeaders.AUTHORIZATION, "T V"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getSapMessageNextIdShouldReturn404IfEncounteredS3ClientException() throws Exception {
        when(sapMessageService.getNextSapMessage(100L))
                .thenThrow(new S3ClientException("---"));
        mvc.perform(MockMvcRequestBuilders.get("/sap_message/next?id=100")
                        .header(HttpHeaders.AUTHORIZATION, "T V"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void canGetSapMessageNextIdIfSapMessageFoundById() throws Exception {
        when(sapMessageService.getNextSapMessage(100L))
                .thenReturn(SapMessageDto.builder().build());
        mvc.perform(MockMvcRequestBuilders.get("/sap_message/next?id=100")
                        .header(HttpHeaders.AUTHORIZATION, "T V"))
                .andExpect(status().isOk());
    }

    @Test
    void getSapMessageNextIdShouldReturn404IfMessageNotFoundByIdWhenNotProvidedMessageId() throws Exception {
        when(sapMessageService.getNextSapMessage(isNull()))
                .thenThrow(new DataNotFoundException("---"));
        mvc.perform(MockMvcRequestBuilders.get("/sap_message/next")
                        .header(HttpHeaders.AUTHORIZATION, "T V"))
                .andExpect(status().isNotFound());
    }

    @Test
    void canGetSapMessageNextIdIfFoundWhenNotProvidedMessageId() throws Exception {
        when(sapMessageService.getNextSapMessage(isNull()))
                .thenReturn(SapMessageDto.builder().build());
        mvc.perform(MockMvcRequestBuilders.get("/sap_message/next")
                        .header(HttpHeaders.AUTHORIZATION, "T V"))
                .andExpect(status().isOk());
    }

    private ZORDERS051 getTestZorder() {
        var E1EDK01 = new ZORDERS051E1EDK01();
        E1EDK01.setBELNR("12345");
        var idoc = new ZORDRSPORDERS05ZORDERS051();
        idoc.setE1EDK01(E1EDK01);
        ZORDERS051 zorder = new ZORDERS051();
        zorder.setIDOC(idoc);
        return zorder;
    }

    private ZMMORDERS05DOP getTestZmmorder() {
        var E1EDK01 = new ZMMORDERS05DOP.IDOC.E1EDK01();
        E1EDK01.setBELNR("12345");
        var idoc = new ZMMORDERS05DOP.IDOC();
        idoc.setE1EDK01(E1EDK01);
        ZMMORDERS05DOP zmmorder = new ZMMORDERS05DOP();
        zmmorder.setIDOC(idoc);
        return zmmorder;
    }

    private String getTestProdAttResDtoJson() throws JsonProcessingException {
        return objectMapper.writeValueAsString(
                ProductAttestationResultDto.builder()
                        .result(ProductDto.builder().build())
                        .kceh(Kceh.PGP)
                        .build()
        );
    }
}
