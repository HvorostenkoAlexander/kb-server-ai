package com.nlmk.kb.server.service;

import com.nlmk.kb.server.entity.CcmAttestationRequestMessage;
import nlmk.l3.ccm.pgp.AttestationRequest;

public interface CcmMessageConverter {

    CcmAttestationRequestMessage fromCcmAttestationRequest(AttestationRequest ccmAttestationRequest,
                                                           String topic,
                                                           String key,
                                                           int partition,
                                                           int offset,
                                                           String timestamp);
}
