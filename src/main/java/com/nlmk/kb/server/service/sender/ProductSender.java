package com.nlmk.kb.server.service.sender;

import com.nlmk.attestation.product.api.pam.ProductAttestationResultDto;

/**
 * Отправка результата Аттестации в другие сервисы
 */
public interface ProductSender {

    /**
     * Отправка результата Аттестации ЕП
     *
     * @param productAttestationResult объект результата Аттестации ЕП
     * @param sendingType              отправляемый тип
     */
    void send(ProductAttestationResultDto productAttestationResult, Class<?> sendingType);

}
