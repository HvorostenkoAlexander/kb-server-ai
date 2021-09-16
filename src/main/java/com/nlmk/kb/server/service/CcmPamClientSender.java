package com.nlmk.kb.server.service;

import com.nlmk.kb.server.entity.pam.AttestationRequest;

public interface CcmPamClientSender {

    public Long postAttestationRequest(AttestationRequest pamAttestetionRequest);
}
