package com.nlmk.kb.server.service;

import nlmk.l3.ccm.pgp.AttestationRequest;

public interface AttestationRequestConverter {

    com.nlmk.kb.server.entity.pam.AttestationRequest toPamAttestationRequest(AttestationRequest request);
}
