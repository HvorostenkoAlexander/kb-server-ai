package com.nlmk.kb.server.service.result.sending;

import com.nlmk.attestation.product.api.ProductDto;

import java.util.Optional;

public interface CommonConditionFilter {

    Optional<ProductDto> filter(ProductDto product, String condition);

}
