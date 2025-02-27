package com.nlmk.kb.server.service.sender;

import com.nlmk.kb.server.entity.Operation;

public interface NsiSender {

    /**
     * Отправка данных в заданный справочник НСИ (ответ число)
     */
    <T> Long sendBodyReturnLong(T body, String targetPath, Operation operation, Long messageId);

    /**
     * Отправка данных в заданный справочник НСИ (ответ строка)
     */
    <T> String sendBodyReturnString(T body, String targetPath, Operation operation, Long messageId);

}
