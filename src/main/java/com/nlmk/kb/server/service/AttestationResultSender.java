package com.nlmk.kb.server.service;

import com.nlmk.attestation.product.api.pam.ProductAttestationResultDto;

/**
 * Отправка результата Аттестации в другие сервисы
 */
public interface AttestationResultSender {

    /**
     * Отправка результата Аттестации ЕП
     *
     * @param productAttestationResult объект результата Аттестации ЕП
     */
    void send(ProductAttestationResultDto productAttestationResult);

}
