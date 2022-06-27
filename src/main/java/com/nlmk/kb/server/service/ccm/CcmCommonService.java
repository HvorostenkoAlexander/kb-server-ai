package com.nlmk.kb.server.service.ccm;

import com.nlmk.kb.server.entity.CcmMessage;

public interface CcmCommonService {

    Long rePostAttestation(String primeId) throws IllegalArgumentException;

    void postAttestation(CcmMessage request);

}
