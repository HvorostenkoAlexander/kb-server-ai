package com.nlmk.kb.server.service.pdm;

import com.nlmk.kb.server.entity.pdm.PdmMessage;

public interface NsiClientService {

    /**
     * Передача сведений из справочников PDM в nsi-server
     */
    Long sendPdmMessage(PdmMessage message);

}
