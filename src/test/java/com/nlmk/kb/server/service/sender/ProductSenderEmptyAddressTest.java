package com.nlmk.kb.server.service.sender;

import com.nlmk.attestation.product.api.AttestationDto;
import com.nlmk.attestation.product.api.ProductDto;
import com.nlmk.attestation.product.api.RequestDto;
import com.nlmk.attestation.product.api.Status;
import com.nlmk.attestation.product.api.pam.ProductAttestationResultDto;
import com.nlmk.kb.server.api.ResultsConfigDto;
import com.nlmk.kb.server.exception.KafkaRestConfigException;
import com.nlmk.kb.server.service.result.configuration.ResultConfigService;
import com.nlmk.kb.server.service.result.sending.KcehConditionFilterImpl;
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
class ProductSenderEmptyAddressTest {

    @Autowired
    private ProductSender productSender;
    @Autowired
    private KcehConditionFilterImpl kcehConditionFilter;
    @MockBean
    private ResultConfigService resultConfigService;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry dpr) {
        dpr.add("service-web-client.kafka-rest.address", () -> ""); // !
        dpr.add("service-web-client.kafka-rest.login", () -> "kb-user");
        dpr.add("service-web-client.kafka-rest.password", () -> "qwe123");
    }

    @Test
    void sendProduct() throws Exception {
        // конфигурация
        Mockito.when(resultConfigService.getEnabledTopics())
                .thenReturn(List.of(
                        ResultsConfigDto.builder().id(2L).topic("topic2").condition(null)
                                // нужная конфигурация
                                .avroName("Передача результатов аттестации APCS. Version: [1]. PGP").enabled(true).build()
                ));

        // минимально полный результат
        final var attResult = ProductAttestationResultDto.builder()
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

        final var e = Assertions.assertThrows(KafkaRestConfigException.class, () -> productSender.send(attResult));
        Assertions.assertEquals("Не установлен адрес сервера kafka-rest. Передача данных невозможна.", e.getMessage());
    }

}
