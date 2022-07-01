package com.nlmk.kb.server.service.ccm;

/**
 * Адаптер в тип <code>AttestationMessage</code>
 *
 * @param <M> сообщение Rest, запрос заданного типа
 */
public interface RestRequestAdapter<M> {

    /**
     * Адаптировать REST запрос в тип <code>AttestationMessage</code>
     *
     * @param requestMessage сообщение Rest, запрос заданного типа
     * @return экземпляр объекта <code>AttestationMessage</code>
     */
    com.nlmk.kb.server.entity.AttestationMessage adapt(M requestMessage);

}
