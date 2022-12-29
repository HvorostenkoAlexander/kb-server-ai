package com.nlmk.kb.server.service.sender;

import com.nlmk.kb.server.entity.Operation;

public interface NsiSender {

    /**
     * Отправка данных в заданный справочник НСИ (ответ число)
     */
    <T> Long sendBodyReturnLong(T body,
                                final String targetPath,
                                final Operation operation);

    /**
     * Отправка данных в заданный справочник НСИ (ответ строка)
     */
    <T> String sendBodyReturnString(T body,
                                    final String targetPath,
                                    final Operation operation);

}
