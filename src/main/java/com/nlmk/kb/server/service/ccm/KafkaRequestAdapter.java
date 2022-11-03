package com.nlmk.kb.server.service.ccm;

/**
 * Адаптер в тип <code>AttestationRequest</code>
 *
 * @param <T> сообщение Kafka, запрос заданного типа
 */
public interface KafkaRequestAdapter<T> {

    /**
     * Адаптировать сообщение Kafka в тип <code>AttestationRequest</code>
     *
     * @param requestMessage сообщение Kafka, запрос заданного типа
     * @return экземпляр объекта <code>AttestationRequest</code>
     */
    com.nlmk.attestation.product.api.pam.AttestationRequest adapt(T requestMessage);

}
