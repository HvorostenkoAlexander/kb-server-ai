package com.nlmk.kb.server.service.ccm;

/**
 * Адаптер в тип <code>AttestationRequest</code>
 *
 * @param <M> сообщение Kafka, запрос заданного типа
 */
public interface AttestationRequestAdapter<M> {

    /**
     * Адаптировать сообщение Kafka в тип <code>AttestationRequest</code>
     *
     * @param requestMessage сообщение Kafka, запрос заданного типа
     * @return экземпляр объекта <code>AttestationRequest</code>
     */
    com.nlmk.kb.server.entity.pam.AttestationRequest adapt(M requestMessage);

}
