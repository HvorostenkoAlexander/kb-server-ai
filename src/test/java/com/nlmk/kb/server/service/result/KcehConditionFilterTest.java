package com.nlmk.kb.server.service.result;

import com.nlmk.attestation.product.api.AttestationDto;
import com.nlmk.attestation.product.api.Group;
import com.nlmk.attestation.product.api.ProductDto;
import com.nlmk.attestation.product.api.RequestDto;
import com.nlmk.attestation.product.api.SpecificationDto;
import com.nlmk.attestation.product.api.Status;
import com.nlmk.attestation.product.api.specification.TypeCode;
import com.nlmk.kb.server.service.result.sending.KcehConditionFilterImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class KcehConditionFilterTest {

    private KcehConditionFilterImpl conditionFilter;

    @BeforeEach
    void setUp() {
        conditionFilter = new KcehConditionFilterImpl();
    }

    @Test
    void testOk() {
        final var validProduct = createProductWithKceh(11);

        final var product = conditionFilter.filter(validProduct, "kceh=11");

        assertThat(product).isPresent().hasValueSatisfying(productDto ->
                assertThat(productDto.getRequests()).hasSize(1)
        );
    }

    @Test
    void testEmptyProductOk() {
        final var validProduct = createProductWithKceh(11);

        final var product = conditionFilter.filter(validProduct, "kceh=0");

        assertThat(product).isPresent().hasValueSatisfying(productDto ->
                assertThat(productDto.getRequests()).isEmpty()
        );
    }

    @Test
    void testKcehNull() {
        final var validProduct = createProductWithKceh(null);

        final var product = conditionFilter.filter(validProduct, "kceh=0");

        assertThat(product).isPresent().hasValueSatisfying(productDto ->
                assertThat(productDto.getRequests()).isEmpty()
        );
    }

    @Test
    void testConditionKcehNull() {
        final var validProduct = createProductWithKceh(11);

        final var product = conditionFilter.filter(validProduct, null);

        assertThat(product).isPresent().hasValueSatisfying(productDto ->
                assertThat(productDto.getRequests()).hasSize(1)
        );
    }

    @Test
    void testNoFilter() {
        final var validProduct = createProductWithKceh(1);

        final var product = conditionFilter.filter(validProduct, "kceh=123XXX");

        assertThat(product).isPresent().hasValueSatisfying(productDto ->
                assertThat(productDto.getRequests()).hasSize(1)
        );
    }

    private ProductDto createProductWithKceh(Integer kceh) {
        return ProductDto.builder()
                .name("p1").referenceId("ref1").referenceCode("code1").createdAt(new Date(1_100_000_000L))
                .requests(List.of(
                        RequestDto.builder()
                                .ts(new Date(1_100_000_000L))
                                .attestationTs(new Date(1_100_000_000L))
                                .status(Status.NOT_MATCHED)
                                .specifications(List.of(SpecificationDto.builder()
                                        .code(93).value("993").typeCode(TypeCode.STRING)
                                        .build()))
                                .attestations(List.of(AttestationDto.builder()
                                        .code(1).value("1").group(Group.ASKIT).status(Status.MATCHED).build()))
                                .kceh(kceh)
                                .build()
                )).build();
    }

}
