package com.nlmk.kb.server.service.result_config;

import com.nlmk.attestation.product.api.ProductDto;

public interface MessageProducer {

    void produce(ProductDto product, boolean isNew , String topic);

    String getType();

}
