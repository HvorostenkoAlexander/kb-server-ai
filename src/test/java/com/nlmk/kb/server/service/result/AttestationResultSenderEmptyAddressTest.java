package com.nlmk.kb.server.service.result;

import com.nlmk.attestation.product.api.AttestationDto;
import com.nlmk.attestation.product.api.ProductDto;
import com.nlmk.attestation.product.api.RequestDto;
import com.nlmk.attestation.product.api.Status;
import com.nlmk.attestation.product.api.pam.ProductAttestationResultDto;
import com.nlmk.kb.server.api.ResultsConfigDto;
import com.nlmk.kb.server.exception.KafkaRestConfigException;
import com.nlmk.kb.server.service.result.configuration.ResultConfigService;
import com.nlmk.kb.server.service.result.sending.AttestationResultSender;
import nlmk.l3.apcs.VerificationResults;
import nlmk.l3.apcs.VerificationResultsPts;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.Date;
import java.util.List;

@SpringBootTest
class AttestationResultSenderEmptyAddressTest {

    @Autowired
    private AttestationResultSender productSender;
    @MockBean
    private ResultConfigService resultConfigService;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry dpr) {
        dpr.add("service-web-client.kafka-rest.address", () -> ""); // !
    }

    private ProductAttestationResultDto prepareMinimal() {
        return ProductAttestationResultDto.builder()
                .result(ProductDto.builder()
                        .id(100L).referenceId("100").referenceCode("1")
                        .requests(List.of(
                                RequestDto.builder()
                                        .id(10L).primeID("100").status(Status.NOT_MATCHED)
                                        .orderNum(12345L).orderPos(4)
                                        .attestationTs(new Date(1_000_000_000L))
                                        .attestations(List.of(
                                                AttestationDto.builder().code(5).value("50")
                                                        .status(Status.NOT_MATCHED).equal("100")
                                                        .build()
                                        ))
                                        .build()
                        ))
                        .build()).newProduct(false)
                .build();
    }

    private List<ResultsConfigDto> prepareConfig() {
        return List.of(
                ResultsConfigDto.builder().id(12).topic("topic12").condition(null)
                        .avroName("VerificationResults").enabled(true).build(),
                ResultsConfigDto.builder().id(11).topic("topic11").condition(null)
                        .avroName("VerificationResultsPts").enabled(true).build()
        );
    }

    @Test
    void sendProductPgp() {
        // нужная конфигурация
        Mockito.when(resultConfigService.getEnabledTopics()).thenReturn(prepareConfig());
        // минимально полный результат
        final var attResult = prepareMinimal();

        final var e = Assertions.assertThrows(KafkaRestConfigException.class,
                () -> productSender.send(attResult, VerificationResults.class));
        Assertions.assertEquals("sending, kafka-rest.address не задан", e.getMessage());
    }

    @Test
    void sendProductPts() {
        // нужная конфигурация
        Mockito.when(resultConfigService.getEnabledTopics()).thenReturn(prepareConfig());
        // минимально полный результат
        final var attResult = prepareMinimal();

        final var e = Assertions.assertThrows(KafkaRestConfigException.class, () -> productSender.send(attResult, VerificationResultsPts.class));
        Assertions.assertEquals("sending, kafka-rest.address не задан", e.getMessage());
    }

}
