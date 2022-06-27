package com.nlmk.kb.server.service.ccm.pts;

public interface AttestationRequestPtsConverter {

    com.nlmk.kb.server.entity.pam.AttestationRequest toPamAttestationRequest(nlmk.l3.ccm.pts.AttestationRequest ccmPtsRequest);

}
