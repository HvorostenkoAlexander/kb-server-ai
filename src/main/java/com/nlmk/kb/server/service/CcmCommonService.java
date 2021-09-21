package com.nlmk.kb.server.service;

import com.nlmk.kb.server.entity.CcmAttestationRequestMessage;

public interface CcmCommonService {

    void rePostAttestation(String primeId);
    void postAttestation(CcmAttestationRequestMessage request);
}
