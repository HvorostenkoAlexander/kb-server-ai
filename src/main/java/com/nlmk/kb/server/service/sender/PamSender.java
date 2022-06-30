package com.nlmk.kb.server.service.sender;

import com.nlmk.attestation.product.api.pam.ProductAttestationResultDto;
import com.nlmk.kb.server.api.pam.AttestationRequest;

public interface PamSender {

    /**
     * Отправка запроса на Аттестацию
     *
     * @param request данные запроса на Аттестацию
     * @return объект ответа результата Аттестации
     */
    ProductAttestationResultDto postAttestationRequest(AttestationRequest request);

}
