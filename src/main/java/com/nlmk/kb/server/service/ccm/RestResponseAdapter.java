package com.nlmk.kb.server.service.ccm;

import com.nlmk.attestation.product.api.pam.ProductAttestationResultDto;

/**
 * Адаптер в тип <code>T</code>
 *
 * @param <T> сообщение Rest, ответ заданного типа
 */
public interface RestResponseAdapter<T> {

    /**
     * Адаптировать результат Аттестации <code>ProductAttestationResultDto</code> в ответ заданного типа <code>M</code>
     *
     * @param attResult результат Аттестации
     * @return экземпляр объекта заданного типа <code>T</code>
     */
    T adapt(ProductAttestationResultDto attResult);

}
