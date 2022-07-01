package com.nlmk.kb.server.service.ccm;

import com.nlmk.attestation.product.api.pam.ProductAttestationResultDto;

/**
 * Адаптер в тип <code>M</code>
 *
 * @param <M> сообщение Rest, ответ заданного типа
 */
public interface RestResponseAdapter<M> {

    /**
     * Адаптировать результат Аттестации <code>ProductAttestationResultDto</code> в ответ заданного типа <code>M</code>
     *
     * @param attResult результат Аттестации
     * @return экземпляр объекта заданного типа <code>M</code>
     */
    M adapt(ProductAttestationResultDto attResult);

}
