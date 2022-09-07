package com.nlmk.kb.server.service.result.sending.pgp;

import com.nlmk.attestation.product.api.ProductDto;
import nlmk.l3.apcs.VerificationResults;

/**
 * Адаптация Единицы Продукции к формату передачи результата Аттестации для цеха ЦГП
 */
public interface VerificationResultsPgpAdapter {

    VerificationResults adapt(ProductDto product, boolean isNew);

}
