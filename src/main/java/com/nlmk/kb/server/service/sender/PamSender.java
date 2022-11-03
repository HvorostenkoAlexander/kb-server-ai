package com.nlmk.kb.server.service.sender;

import com.nlmk.attestation.product.api.pam.AttestationRequest;
import com.nlmk.attestation.product.api.pam.ProductAttestationResultDto;

public interface PamSender {

    /**
     * Отправка запроса на Аттестацию
     */
    ProductAttestationResultDto postAttestationRequest(AttestationRequest attestationRequest);

}
