package com.nlmk.kb.server.service.sap;

import com.nlmk.attestation.product.api.kb.SapMessageDto;

public interface SapMessageService {

    /**
     * Запрос получения сообщения SAP, следующего после указанного id
     */
    SapMessageDto getNextSapMessage(Long id);

}
