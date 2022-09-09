package com.nlmk.kb.server.service.result.sending;

import com.nlmk.attestation.product.api.ProductDto;

/**
 * Адаптация Единицы Продукции к формату передачи результата Аттестации
 */
public interface ResultAdapter<T> {

    T adapt(ProductDto product, boolean isNew);

}
