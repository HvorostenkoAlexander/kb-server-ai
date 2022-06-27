package com.nlmk.kb.server.service.ccm.pts;

import com.nlmk.kb.server.entity.CcmMessage;
import nlmk.l3.ccm.pts.AttestationRequest;

public interface CcmPtsMessageConverter {

    CcmMessage fromCcmAttestationRequest(AttestationRequest ccmAttestationRequest,
                                         String topic,
                                         String key,
                                         int partition,
                                         int offset,
                                         String timestamp);

}
