package com.nlmk.kb.server.service.ccm;

import com.nlmk.kb.server.entity.pam.AttestationRequest;

public interface CcmPamClientSender {

    Long postAttestationRequest(AttestationRequest pamAttestetionRequest);

}
