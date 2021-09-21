package com.nlmk.kb.server.service;

import com.nlmk.kb.server.entity.CcmAttestationRequestMessage;

public interface CcmCommonService {

    Long rePostAttestation(String primeId) throws IllegalArgumentException;
    Long postAttestation(CcmAttestationRequestMessage request);
}
