package com.nlmk.kb.server.service;

import com.nlmk.attestation.product.api.kb.CdcUpdate;

public interface CaptureDataChangeService {

    void updateMdmMessage(CdcUpdate cdcUpdate);

}
