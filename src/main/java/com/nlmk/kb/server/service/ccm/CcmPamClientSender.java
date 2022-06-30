package com.nlmk.kb.server.service.ccm;

import com.nlmk.attestation.product.api.pam.ProductAttestationResultDto;
import com.nlmk.kb.server.entity.pam.AttestationRequest;

public interface CcmPamClientSender {

    /**
     * Отправка запроса на Аттестацию
     *
     * @param request данные запроса на Аттестацию
     * @return объект ответа результата Аттестации
     */
    ProductAttestationResultDto postAttestationRequest(AttestationRequest request);

}
