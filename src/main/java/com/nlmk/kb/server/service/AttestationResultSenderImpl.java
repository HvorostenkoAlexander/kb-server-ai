package com.nlmk.kb.server.service;

import com.nlmk.attestation.product.api.pam.ProductAttestationResultDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AttestationResultSenderImpl implements AttestationResultSender {

    @Override
    public void send(ProductAttestationResultDto productAttestationResult) {
        final var isNew = productAttestationResult.isNewProduct();
        final var product = productAttestationResult.getResult();

        log.info("send attestation result for product: id [{}], referenceId [{}]", product.getId(), product.getReferenceId());

        // todo
    }

}
