package com.nlmk.kb.server.service;

import com.nlmk.attestation.product.api.AttestationDto;
import com.nlmk.attestation.product.api.ProductDto;
import com.nlmk.attestation.product.api.RequestDto;
import com.nlmk.attestation.product.api.Status;
import com.nlmk.attestation.product.api.pam.ProductAttestationResultDto;
import com.nlmk.kb.server.api.ResultsConfigDto;
import com.nlmk.kb.server.exception.ProductSenderException;
import com.nlmk.kb.server.service.result.configuration.ResultConfigService;
import com.nlmk.kb.server.service.result.sending.KcehConditionFilterImpl;
import com.nlmk.kb.server.service.sender.ProductSender;
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
class ProductSenderTest {

    @Autowired
    private ProductSender productSender;
    @Autowired
    private KcehConditionFilterImpl kcehConditionFilter;
    @MockBean
    private ResultConfigService resultConfigService;

    static MockWebServer mockKafkaRest;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry dpr) {
        dpr.add("kafka.httpProxy.address", () -> "http://localhost:" + mockKafkaRest.getPort());
        dpr.add("kafka.httpProxy.login", () -> "kb-user");
        dpr.add("kafka.httpProxy.password", () -> "qwe123");
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
        Assertions.assertDoesNotThrow(() -> productSender.send(attResult));

        // для заданной AVRO схемы нет подходящей конфигурации (по имени схемы)
        Mockito.when(resultConfigService.getEnabledTopics())
                .thenReturn(List.of(
                        ResultsConfigDto.builder().id(1L).topic("topic1").avroName("avro1").enabled(true).build(),
                        ResultsConfigDto.builder().id(2L).topic("topic2").avroName("avro2").enabled(true).build()
                ));
        Assertions.assertThrows(ProductSenderException.class, () -> productSender.send(attResult));

        // конфигурация есть, но результат аттестации пустой
        Mockito.when(resultConfigService.getEnabledTopics())
                .thenReturn(List.of(
                        ResultsConfigDto.builder().id(1L).topic("topic1").condition(null)
                                .avroName("avro1").enabled(true).build(),
                        ResultsConfigDto.builder().id(2L).topic("topic2").condition(null)
                                // нужная конфигурация
                                .avroName("Передача результатов аттестации APCS. Version: [1]").enabled(true).build()
                ));
        Assertions.assertDoesNotThrow(() -> productSender.send(attResult));
        // передачи еще не было
        Assertions.assertEquals(0, mockKafkaRest.getRequestCount());

        // попытка отправки
        product.setId(100L);
        product.setReferenceCode("1");
        product.setReferenceId("100");
        product.setRequests(List.of(
                RequestDto.builder()
                        .id(10L).primeID("100").status(Status.NOT_MATCHED).orderNum(12345L).orderPos(4L)
                        .attestations(List.of()) // нет результата аттестации
                        .build()
        ));

        Assertions.assertThrows(IllegalArgumentException.class, () -> productSender.send(attResult));
        // передачи еще не было
        Assertions.assertEquals(0, mockKafkaRest.getRequestCount());

        // отправка еще раз
        product.getRequests().get(0).setAttestations(List.of(
                AttestationDto.builder().code(5).value("50").status(Status.NOT_MATCHED).equal("100").build()
        ));
        // требования AVRO схемы не выполнены
        Assertions.assertThrows(AvroRuntimeException.class, () -> productSender.send(attResult));
        // передачи еще не было
        Assertions.assertEquals(0, mockKafkaRest.getRequestCount());

        // отправка еще раз
        product.getRequests().get(0).setAttestationTs(new Date(1_000_000_000L));

        mockKafkaRest.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.OK.value()));

        Assertions.assertDoesNotThrow(() -> productSender.send(attResult));

        RecordedRequest request = mockKafkaRest.takeRequest();
        Assertions.assertEquals("POST", request.getMethod());
        Assertions.assertEquals("/topics/topic2", request.getPath());
        Assertions.assertEquals(1, mockKafkaRest.getRequestCount());
    }

}
