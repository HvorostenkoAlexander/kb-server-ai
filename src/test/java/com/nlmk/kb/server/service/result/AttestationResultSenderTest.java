package com.nlmk.kb.server.service.result;

import com.nlmk.attestation.product.api.AttestationDto;
import com.nlmk.attestation.product.api.ProductDto;
import com.nlmk.attestation.product.api.RequestDto;
import com.nlmk.attestation.product.api.Status;
import com.nlmk.attestation.product.api.pam.ProductAttestationResultDto;
import com.nlmk.kb.server.api.ResultsConfigDto;
import com.nlmk.kb.server.exception.AttestationResultSenderException;
import com.nlmk.kb.server.service.result.configuration.ResultConfigService;
import com.nlmk.kb.server.service.result.sending.AttestationResultSender;
import com.nlmk.kb.server.service.result.sending.KcehConditionFilterImpl;
import nlmk.l3.apcs.VerificationResults;
import nlmk.l3.apcs.VerificationResultsPts;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.apache.avro.AvroRuntimeException;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@SpringBootTest
class AttestationResultSenderTest {

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
    }

    @BeforeAll
    static void setUp() throws IOException {
        mockKafkaRest = new MockWebServer();
        mockKafkaRest.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockKafkaRest.shutdown();
    }

    @Test
    void sendProduct() throws Exception {
        // результат аттестации сначала пустой
        final var product = ProductDto.builder().build();
        final var attResult = ProductAttestationResultDto.builder()
                .result(product).newProduct(false)
                .build();

        Mockito.when(resultConfigService.getEnabledTopics()).thenReturn(List.of());
        Assertions.assertDoesNotThrow(() -> productSender.send(attResult, VerificationResults.class));

        // для заданной AVRO схемы нет подходящей конфигурации (по имени схемы)
        Mockito.when(resultConfigService.getEnabledTopics())
                .thenReturn(List.of(
                        ResultsConfigDto.builder().id(1).topic("topic1").avroName("avro1").enabled(true).build(),
                        ResultsConfigDto.builder().id(2).topic("topic2").avroName("avro2").enabled(true).build()
                ));
        Assertions.assertThrows(AttestationResultSenderException.class, () -> productSender.send(attResult, VerificationResults.class));

        // конфигурация есть, но результат аттестации пустой
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
        // передачи еще не было
        Assertions.assertEquals(0, mockKafkaRest.getRequestCount());

        // попытка отправки
        product.setId(100L);
        product.setReferenceCode("1");
        product.setReferenceId("100");
        product.setRequests(List.of(
                RequestDto.builder()
                        .id(10L).primeID("100").status(Status.NOT_MATCHED).orderNum(12345L).orderPos(4)
                        .attestations(List.of()) // нет результата аттестации
                        .build()
        ));

        Assertions.assertThrows(IllegalArgumentException.class, () -> productSender.send(attResult, VerificationResults.class));
        // передачи еще не было
        Assertions.assertEquals(0, mockKafkaRest.getRequestCount());

        // отправка еще раз
        product.getRequests().get(0).setAttestations(List.of(
                AttestationDto.builder().code(5).value("50").status(Status.NOT_MATCHED).equal("100").build()
        ));
        // требования AVRO схемы не выполнены
        Assertions.assertThrows(AvroRuntimeException.class, () -> productSender.send(attResult, VerificationResults.class));
        // передачи еще не было
        Assertions.assertEquals(0, mockKafkaRest.getRequestCount());

        // отправка еще раз
        product.getRequests().get(0).setAttestationTs(new Date(1_000_000_000L));

        {
            // VerificationResults
            mockKafkaRest.enqueue(new MockResponse()
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setResponseCode(HttpStatus.OK.value()));

            Assertions.assertDoesNotThrow(() -> productSender.send(attResult, VerificationResults.class));

            RecordedRequest request = mockKafkaRest.takeRequest();
            Assertions.assertEquals("POST", request.getMethod());
            Assertions.assertEquals("/topics/topic2", request.getPath());
            Assertions.assertEquals(1, mockKafkaRest.getRequestCount());
        }
        {
            // VerificationResultsPts
            mockKafkaRest.enqueue(new MockResponse()
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setResponseCode(HttpStatus.OK.value()));

            Assertions.assertDoesNotThrow(() -> productSender.send(attResult, VerificationResultsPts.class));

            RecordedRequest request = mockKafkaRest.takeRequest();
            Assertions.assertEquals("POST", request.getMethod());
            Assertions.assertEquals("/topics/topic3", request.getPath());
            Assertions.assertEquals(2, mockKafkaRest.getRequestCount());
        }
    }

}
