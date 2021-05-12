package com.nlmk.kb.server.service;

import com.nlmk.kb.server.entity.PdmMessage;
import org.springframework.http.ResponseEntity;

public interface NsiClientService {

    /**
     * Передача сведений из справочников PDM в nsi-server
     * @param message из БД kb-server
     */

    public ResponseEntity<Long> sendPdmDictionary(PdmMessage message);
}
