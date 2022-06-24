package com.nlmk.kb.server.service.ccm;

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
