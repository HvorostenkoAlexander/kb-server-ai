package com.nlmk.kb.server.service;

import com.nlmk.kb.server.entity.pam.AttestationRequest;

public interface PamClientService {

    public Long postAttestationRequest(AttestationRequest pamAttestetionRequest);
}
