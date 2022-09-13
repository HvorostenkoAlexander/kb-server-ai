package com.nlmk.kb.server.service.sender;

import com.nlmk.kb.server.entity.pdm.PdmOp;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

public interface NsiSender {

    /**
     * Отправка данных в заданный справочник НСИ
     */
    <T> Long exchange(T body,
                      final String urlDictionary,
                      final PdmOp operation);

    @Deprecated
    ResponseEntity<Long> exchange(HttpEntity<?> request,
                                  final String urlDictionary,
                                  final PdmOp operation);

}
