package com.nlmk.kb.server.service.result;

import com.nlmk.attestation.product.api.AttestationDto;
import com.nlmk.attestation.product.api.ProductDto;
import com.nlmk.attestation.product.api.RequestDto;
import com.nlmk.attestation.product.api.Status;
import com.nlmk.attestation.product.api.pam.ProductAttestationResultDto;
import com.nlmk.kb.server.api.ResultsConfigDto;
import com.nlmk.kb.server.service.result.configuration.ResultConfigService;
import com.nlmk.kb.server.service.result.sending.AttestationResultSender;
import com.nlmk.kb.server.service.result.sending.KcehConditionFilterImpl;
import nlmk.l3.apcs.VerificationResults;
import nlmk.l3.apcs.VerificationResultsPts;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@SpringBootTest
class AttestationResultSenderBasicAuthTest {

    @Autowired
    private AttestationResultSender productSender;
    @Autowired
    private KcehConditionFilterImpl kcehConditionFilter;
    @MockBean
    private ResultConfigService resultConfigService;

    static MockWebServer mockKafkaRest;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry dpr) {
        dpr.add("service-web-client.kafka-rest.address", () -> "http://localhost:" + mockKafkaRest.getPort());
        dpr.add("service-web-client.kafka-rest.login", () -> "kb-user");
        dpr.add("service-web-client.kafka-rest.password", () -> "qwe123");
    }

    @BeforeAll
    static void setUp() throws IOException {
        mockKafkaRest = new MockWebServer();
        mockKafkaRest.start();

        mockKafkaRest.setDispatcher(new Dispatcher() {
            @NotNull
            @Override
            public MockResponse dispatch(@NotNull RecordedRequest request) throws InterruptedException {
                final var auth = request.getHeader(HttpHeaders.AUTHORIZATION);
                if (StringUtils.isBlank(auth)) {
                    return new MockResponse().setResponseCode(HttpStatus.UNAUTHORIZED.value());
                }
                final String[] basic = auth.split(" ");
                if (basic.length != 2) {
                    return new MockResponse().setResponseCode(HttpStatus.UNAUTHORIZED.value());
                }
                if (!basic[0].equals("Basic")) {
                    return new MockResponse().setResponseCode(HttpStatus.UNAUTHORIZED.value());
                }

                final var actual = new String(Base64.getDecoder().decode(basic[1]));
                final var expected = "kb-user:qwe123";

                if (!actual.equals(expected)) {
                    return new MockResponse().setResponseCode(HttpStatus.UNAUTHORIZED.value());
                }

                return new MockResponse().setResponseCode(HttpStatus.OK.value());
            }
        });
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockKafkaRest.shutdown();
    }

    @Test
    void basicAuth() {
        final var attResult = ProductAttestationResultDto.builder()
                .newProduct(false)
                .result(ProductDto.builder()
                        .id(100L)
                        .referenceCode("1")
                        .referenceId("100")
                        .requests(List.of(
                                RequestDto.builder()
                                        .id(10L).primeID("100")
                                        .status(Status.NOT_MATCHED)
                                        .orderNum(12345L).orderPos(4)
                                        .attestationTs(new Date(1_000_000_000L))
                                        .attestations(List.of(
                                                AttestationDto.builder()
                                                        .code(5)
                                                        .value("50")
                                                        .status(Status.NOT_MATCHED)
                                                        .equal("100").build()
                                        ))
                                        .build()
                        ))
                        .build())
                .build();

        Mockito.when(resultConfigService.getEnabledTopics())
                .thenReturn(List.of(
                        ResultsConfigDto.builder().id(1).topic("topic1").condition(null)
                                .avroName("avro1").enabled(true).build(),
                        // нужная конфигурация
                        ResultsConfigDto.builder().id(2).topic("topic2").condition(null)
                                .avroName("VerificationResults").enabled(true).build(),
                        ResultsConfigDto.builder().id(3).topic("topic3").condition(null)
                                .avroName("VerificationResultsPts").enabled(true).build()
                ));

        Assertions.assertDoesNotThrow(() -> productSender.send(attResult, VerificationResults.class));
        Assertions.assertDoesNotThrow(() -> productSender.send(attResult, VerificationResultsPts.class));
    }

}
