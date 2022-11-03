package com.nlmk.kb.server.service.result.sending;

import com.nlmk.attestation.product.api.pam.ProductAttestationResultDto;

/**
 * Отправка результата Аттестации в другие сервисы
 */
public interface AttestationResultSender {

    /**
     * Отправка результата Аттестации ЕП
     *
     * @param productAttestationResult объект результата Аттестации ЕП
     * @param sendingType              отправляемый тип
     */
    void send(ProductAttestationResultDto productAttestationResult, Class<?> sendingType);

}
