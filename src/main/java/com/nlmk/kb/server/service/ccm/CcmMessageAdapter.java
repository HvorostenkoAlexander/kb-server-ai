package com.nlmk.kb.server.service.ccm;

/**
 * Адаптер в тип <code>CcmMessage</code>
 *
 * @param <T> сообщение Kafka, запрос заданного типа
 */
public interface CcmMessageAdapter<T> {

    /**
     * Адаптировать сообщение Kafka в тип <code>CcmMessage</code>
     *
     * @param requestMessage сообщение Kafka, запрос заданного типа
     * @param topic          тема сообщения Kafka
     * @param key            ключ сообщения Kafka
     * @param partition      секция сообщения Kafka
     * @param offset         смещение сообщения Kafka
     * @return экземпляр объекта <code>CcmMessage</code>
     */
    com.nlmk.kb.server.entity.CcmMessage adapt(T requestMessage,
                                               String topic,
                                               String key,
                                               int partition,
                                               int offset);

}
