package com.nlmk.kb.server;

import com.nlmk.attestation.product.api.ProductDto;
import nlmk.l3.apcs.VerificationResults;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class VerificationResultTest {

    @Test
    @Disabled
    void test() {
        VerificationResults results = new VerificationResults();
        ProductDto productDto = new ProductDto();
    }

    private VerificationResults converter(ProductDto productDto) {

        VerificationResults results = VerificationResults.newBuilder()
                .setTs(productDto.getCreatedAt().toString())
                .build();

        return null;
    }
}
