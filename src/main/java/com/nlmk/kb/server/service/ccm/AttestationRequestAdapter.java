package com.nlmk.kb.server.service.ccm;

import com.nlmk.kb.server.api.pam.AttestationRequest;

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
    AttestationRequest adapt(M requestMessage);

}
