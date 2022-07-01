package com.nlmk.kb.server.service.ccm.pts;

import com.nlmk.attestation.product.api.pam.ProductAttestationResultDto;
import com.nlmk.kb.server.api.CcmPtsResponse;
import com.nlmk.kb.server.service.ccm.RestResponseAdapter;
import org.springframework.stereotype.Component;

@Component
public class CcmPtsRestResponseAdapterImpl implements RestResponseAdapter<CcmPtsResponse> {

    @Override
    public CcmPtsResponse adapt(ProductAttestationResultDto attResult) {
        // todo
        return CcmPtsResponse.builder().build();
    }

}
