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
import nlmk.l3.apcs.VerificationResultsKc1;
import nlmk.l3.apcs.VerificationResultsKc2;
import nlmk.l3.apcs.VerificationResultsPts;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@SpringBootTest
class AttestationResultSenderEmptyAddressTest {

    @Autowired
    private AttestationResultSender productSender;
    @MockBean
    private ResultConfigService resultConfigService;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry dpr) {
        dpr.add("service-web-client.kafka-rest.address", () -> "");
    }

    private ProductAttestationResultDto prepareMinimal() {
        return ProductAttestationResultDto.builder()
                .result(ProductDto.builder()
                        .id(100L).referenceId("100").referenceCode("1")
                        .requests(List.of(RequestDto.builder()
                                .id(10L).primeID("100").status(Status.NOT_MATCHED)
                                .orderNum(12345L).orderPos(4)
                                .attestationTs(new Date(1_000_000_000L))
                                .attestations(List.of(AttestationDto.builder().code(5).value("50")
                                        .status(Status.NOT_MATCHED).equal("100")
                                        .build()
                                )).build()
                        )).build()).newProduct(false)
                .build();
    }

    private List<ResultsConfigDto> prepareConfig() {
        return List.of(
                ResultsConfigDto.builder().id(12).topic("topic12").condition(null)
                        .avroName("VerificationResults").enabled(true).build(),
                ResultsConfigDto.builder().id(11).topic("topic11").condition(null)
                        .avroName("VerificationResultsPts").enabled(true).build(),
                ResultsConfigDto.builder().id(6).topic("topic6").condition(null)
                        .avroName("VerificationResultsKc1").enabled(true).build(),
                ResultsConfigDto.builder().id(7).topic("topic7").condition(null)
                        .avroName("VerificationResultsKc2").enabled(true).build()
        );
    }

    @ParameterizedTest
    @MethodSource("verificationResultClasses")
    public void sendProductVerificationResult(Class<?> verificationResultClass) {
        // нужная конфигурация
        when(resultConfigService.getEnabledTopics()).thenReturn(prepareConfig());
        // минимально полный результат
        final var attResult = prepareMinimal();

        assertThatThrownBy(() -> productSender.send(attResult, verificationResultClass))
                .isInstanceOf(KafkaRestConfigException.class)
                .hasMessage("sending, kafka-rest.address не задан");
    }

    private static Stream<Arguments> verificationResultClasses() {
        return Stream.of(Arguments.of(VerificationResults.class),
                Arguments.of(VerificationResultsPts.class),
                Arguments.of(VerificationResultsKc1.class),
                Arguments.of(VerificationResultsKc2.class));
    }

}
