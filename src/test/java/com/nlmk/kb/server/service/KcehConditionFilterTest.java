package com.nlmk.kb.server.service;

import com.nlmk.attestation.product.api.*;
import com.nlmk.kb.server.service.result.sending.KcehConditionFilterImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class KcehConditionFilterTest {

    private KcehConditionFilterImpl conditionFilter;

    @BeforeEach
    void setUp() {
        conditionFilter = new KcehConditionFilterImpl();
    }

    @Test
    void testOk() {
        final var validProduct = createProductWithKceh(11L);

        final var product = conditionFilter.filter(validProduct, "kceh=11");

        assertNotNull(product);
        Assertions.assertTrue(product.isPresent());
        Assertions.assertEquals(1, product.get().getRequests().size());
    }

    @Test
    void testEmptyProductOk() {
        final var validProduct = createProductWithKceh(11L);

        final var product = conditionFilter.filter(validProduct, "kceh=0");

        assertNotNull(product);
        Assertions.assertTrue(product.isPresent());
        Assertions.assertEquals(0, product.get().getRequests().size());
    }

    @Test
    void testKcehNull() {
        final var validProduct = createProductWithKceh(null);

        final var product = conditionFilter.filter(validProduct, "kceh=0");

        assertNotNull(product);
        Assertions.assertTrue(product.isPresent());
        Assertions.assertEquals(0, product.get().getRequests().size());
    }

    @Test
    void testConditionKcehNull() {
        final var validProduct = createProductWithKceh(11L);

        final var product = conditionFilter.filter(validProduct, null);

        assertNotNull(product);
        Assertions.assertTrue(product.isPresent());
        Assertions.assertEquals(1, product.get().getRequests().size());
    }

    @Test
    void testNoFilter() {
        final var validProduct = createProductWithKceh(1L);

        final var product = conditionFilter.filter(validProduct, "kceh=123XXX");

        assertNotNull(product);
        Assertions.assertTrue(product.isPresent());
        Assertions.assertEquals(1, product.get().getRequests().size());
    }

    private ProductDto createProductWithKceh(Long kceh) {
        return ProductDto.builder()
                .name("p1").referenceId("ref1").referenceCode("code1").createdAt(new Date(1_100_000_000L))
                .requests(List.of(
                        RequestDto.builder()
                                .ts(new Date(1_100_000_000L))
                                .attestationTs(new Date(1_100_000_000L))
                                .status(Status.NOT_MATCHED)
                                .specifications(List.of(
                                        SpecificationDto.builder()
                                                .code(93).value("993").typeCode(TypeCode.STRING)
                                                .build()
                                ))
                                .attestations(List.of(
                                        AttestationDto.builder()
                                                .code(1).value("1").group(Group.ASKIT).status(Status.MATCHED).build()
                                ))
                                .kceh(kceh)
                                .build()
                )).build();
    }

}
