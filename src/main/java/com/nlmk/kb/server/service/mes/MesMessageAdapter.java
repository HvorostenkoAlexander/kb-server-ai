package com.nlmk.kb.server.service.mes;

/**
 * Адаптер в тип <code>MesMessage</code>
 *
 * @param <T> сообщение Kafka, запрос заданного типа
 */
public interface MesMessageAdapter<T> {

    /**
     * Адаптировать сообщение Kafka в тип <code>MesMessage</code>
     *
     * @param requestMessage сообщение Kafka, запрос заданного типа
     * @param topic          тема сообщения Kafka
     * @param key            ключ сообщения Kafka
     * @param partition      секция сообщения Kafka
     * @param offset         смещение сообщения Kafka
     * @return экземпляр объекта <code>CcmMessage</code>
     */
    com.nlmk.kb.server.entity.MesMessage adapt(T requestMessage,
                                               String topic,
                                               String key,
                                               int partition,
                                               int offset);

}
