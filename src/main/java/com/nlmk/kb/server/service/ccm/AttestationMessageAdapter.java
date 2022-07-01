package com.nlmk.kb.server.service.ccm;

/**
 * Адаптер в тип <code>AttestationMessage</code>
 *
 * @param <M> сообщение Rest, запрос заданного типа
 */
public interface AttestationMessageAdapter<M> {

    com.nlmk.kb.server.entity.AttestationMessage adapt(M requestMessage);

}
