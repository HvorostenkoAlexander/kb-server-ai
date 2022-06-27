package com.nlmk.kb.server.service.ccm.pgp;

import com.nlmk.kb.server.entity.CcmMessage;
import nlmk.l3.ccm.pgp.AttestationRequest;

public interface CcmPgpMessageConverter {

    CcmMessage fromCcmAttestationRequest(AttestationRequest ccmAttestationRequest,
                                         String topic,
                                         String key,
                                         int partition,
                                         int offset,
                                         String timestamp);

}
