package com.nlmk.kb.server.service.sender;

import com.nlmk.kb.server.entity.Operation;
import org.springframework.http.ResponseEntity;

public interface NsiSender {

    /**
     * Отправка данных в заданный справочник НСИ (ответ число)
     */
    <T> ResponseEntity<Long> exchange(T body,
                                      final String urlDictionary,
                                      final Operation operation);

    /**
     * Отправка данных в заданный справочник НСИ (ответ строка)
     */
    <T> ResponseEntity<String> exchangeReturnString(T body,
                                                    final String urlDictionary,
                                                    final Operation operation);

}
