package com.nlmk.kb.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nlmk.attestation.product.api.SadimMessageDto;
import com.nlmk.attestation.zorder.ZORDERS051;

public interface PsmSender {

    /**
     * Передача заказа в модуль PSM
     *
     * @param zorder заказ
     * @return количество сохраненных позиций заказа
     */
    Integer postZorder(ZORDERS051 zorder) throws JsonProcessingException;

    /**
     * Отправка на хранение в сервис PSM разобранного сообщения САДиМ
     *
     * @param dto подготовленный объект из сообщения Kafka
     */
    void postSadimMessage(SadimMessageDto dto);

}
