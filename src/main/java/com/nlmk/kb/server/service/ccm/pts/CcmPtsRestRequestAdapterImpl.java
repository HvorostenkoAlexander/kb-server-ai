package com.nlmk.kb.server.service.ccm.pts;

import com.nlmk.attestation.product.api.pam.AttestationRequest;
import com.nlmk.kb.server.api.ccm.pts.CcmPtsRequest;
import com.nlmk.kb.server.service.ccm.RestRequestAdapter;
import org.springframework.stereotype.Component;

@Component
public class CcmPtsRestRequestAdapterImpl implements RestRequestAdapter<CcmPtsRequest> {

    @Override
    public AttestationRequest adapt(CcmPtsRequest requestMessage) {
        return AttestationRequest.builder()
                // todo
                .build();
    }

}
