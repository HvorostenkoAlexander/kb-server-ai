package com.nlmk.kb.server.service.sender;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nlmk.attestation.zmmorder.ZMMORDERS05DOP;
import com.nlmk.attestation.product.api.SadimMessageDto;
import com.nlmk.attestation.zorder.ZORDERS051;

public interface PsmSender {

    /**
     * Передача заказа ZORDERS051 в модуль PSM
     *
     * @param zorder заказ
     * @return количество сохраненных позиций заказа
     */
    Integer postZorder(ZORDERS051 zorder) throws JsonProcessingException;

    /**
     * Передача заказа ZMMORDERS05DOP в модуль PSM
     *
     * @param zmmorder заказ
     * @return количество сохраненных позиций заказа
     */
    Integer postZmmorder(ZMMORDERS05DOP zmmorder) throws JsonProcessingException;

    /**
     * Отправка на хранение в сервис PSM разобранного сообщения САДиМ
     *
     * @param dto подготовленный объект из сообщения Kafka
     */
    void postSadimMessage(SadimMessageDto dto);

}
