package com.nlmk.kb.server.service.ccm;

/**
 * Адаптер в тип <code>com.nlmk.attestation.product.api.pam.AttestationRequest</code>
 *
 * @param <M> сообщение Rest, запрос заданного типа
 */
public interface RestRequestAdapter<M> {

    /**
     * Адаптировать REST запрос в тип <code>com.nlmk.attestation.product.api.pam.AttestationMessage</code>
     *
     * @param requestMessage сообщение Rest, запрос заданного типа
     * @return экземпляр объекта <code>com.nlmk.attestation.product.api.pam.AttestationMessage</code>
     */
    com.nlmk.attestation.product.api.pam.AttestationRequest adapt(M requestMessage);

}
