package com.nlmk.kb.server.service.result.sending;

import com.nlmk.attestation.product.api.ProductDto;

/**
 * Отправитель сообщения заданного типа <code>T</code><br>
 * Тип <code>T</code> и наименование головного объекта Avro-схемы связанны!
 */
public interface MessageProducer<T> {

    /**
     * Подготовка и отправка результата Аттестации в заданном типе
     */
    void produce(ProductDto product, boolean isNew, String topic);

    /**
     * Наименования головного объекта Avro-схемы
     */
    String getAvroName();

    /**
     * Какой тип данных должен быть отправлен (Class)
     */
    Class<T> getSendingType();

}
