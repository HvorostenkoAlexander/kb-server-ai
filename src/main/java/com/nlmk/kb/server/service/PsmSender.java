package com.nlmk.kb.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nlmk.attestation.zorder.ZORDERS051;

public interface PsmSender {

    /**
     * Передача заказа в модуль PSM
     *
     * @param zorder заказ
     * @return количество сохраненных позиций заказа
     */
    Integer postZorder(ZORDERS051 zorder) throws JsonProcessingException;

}
