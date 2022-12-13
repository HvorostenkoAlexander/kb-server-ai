package com.nlmk.kb.server.service.sender;

import com.nlmk.kb.server.entity.Operation;
import org.springframework.http.ResponseEntity;

public interface NsiSender {

    /**
     * Отправка данных в заданный справочник НСИ (ответ число)
     */
    <T> ResponseEntity<Long> sendBodyReturnLong(T body,
                                                final String targetPath,
                                                final Operation operation);

    /**
     * Отправка данных в заданный справочник НСИ (ответ строка)
     */
    <T> ResponseEntity<String> sendBodyReturnString(T body,
                                                    final String targetPath,
                                                    final Operation operation);

}
