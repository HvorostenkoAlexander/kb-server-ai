package com.nlmk.kb.server.service.result.sending;

import com.nlmk.attestation.product.api.ProductDto;
import nlmk.l3.apcs.VerificationResults;

/**
 * Адаптация формата Единицы Продукции с результатами Аттестации к формату передачи результата Аттестации
 */
public interface VerificationResultsAdapter {

    VerificationResults adapt(ProductDto product, boolean isNew);

}
