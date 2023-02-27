package com.nlmk.kb.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.attestation.product.api.ProductDto;
import com.nlmk.attestation.product.api.RequestDto;
import com.nlmk.attestation.product.api.pam.ProductAttestationResultDto;
import com.nlmk.kb.server.api.ccm.pts.CcmPtsRequest;
import com.nlmk.kb.server.repository.AttestationMessageRepository;
import com.nlmk.kb.server.repository.CcmMessageSourceRepository;
import com.nlmk.kb.server.service.sender.PamSender;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@SpringBootTest
public class AttestationMessageServiceTest {

    @Autowired
    private AttestationMessageRepository attestationMessageRepository;
    @Autowired
    private CcmMessageSourceRepository ccmMessageSourceRepository;
    @Autowired
    private PamSender pamSender;
    @Autowired
    private AttestationMessageService attestationMessageService;

    static MockWebServer mockWebServer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry dpr) {
        dpr.add("service-web-client.pam-server.url", () -> "http://localhost:" + mockWebServer.getPort());
    }

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    private CcmPtsRequest prepareCcmPtsRequest() {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        return CcmPtsRequest.builder()
                .ts(sdf.format(new Date(1000000000_000L))) // для теста!
                .pk(CcmPtsRequest.Pk.builder().systemCode("11").id("22").build())
                .data(CcmPtsRequest.Record.builder()
                        .marking(CcmPtsRequest.Marking.builder()
                                .nplv(2106684).hnum(25217).tnum(22).roll(1).build())
                        .geometry(CcmPtsRequest.Geometry.builder()
                                .thickness(BigDecimal.valueOf(30.0))
                                .width(BigDecimal.valueOf(300.0))
                                .length(BigDecimal.valueOf(3000.0)).build())
                        .properties(List.of())
                        .specifications(List.of())
                        .chemical(List.of())
                        .build())
                .build();
    }

    @Test
    void ccmPtsRequestProcessing() throws Exception {
        // первоначальный запрос
        final var request = prepareCcmPtsRequest();

        mockWebServer.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.OK.value())
                .setBody(objectMapper.writeValueAsString(
                        ProductAttestationResultDto.builder()
                                .newProduct(true)
                                .result(ProductDto.builder()
                                        .id(100L)
                                        .requests(List.of(RequestDto.builder().id(200L).build()))
                                        .build())
                                .build()
                ))
        );

        Assertions.assertDoesNotThrow(() -> attestationMessageService.ccmPtsRequestProcessing(request));
        mockWebServer.takeRequest();

        // сообщение с запросом в формате PAM сохранено
        final var attMessage = attestationMessageRepository.findFirstByPrimeIdOrderByReceiptTsDesc("22");
        Assertions.assertTrue(attMessage.isPresent());
        // сообщение с первоначальным запросом сохранено
        final var sourceMessage = ccmMessageSourceRepository.findByRequestId(200L);
        Assertions.assertTrue(sourceMessage.isPresent());
        Assertions.assertEquals(objectMapper.writeValueAsString(request), sourceMessage.get().getMessageSource());
    }

}
