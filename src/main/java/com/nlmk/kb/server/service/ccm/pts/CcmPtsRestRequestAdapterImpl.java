package com.nlmk.kb.server.service.ccm.pts;

import com.nlmk.kb.server.api.ccm.pts.CcmPtsRequest;
import com.nlmk.kb.server.entity.AttestationMessage;
import com.nlmk.kb.server.service.ccm.RestRequestAdapter;
import org.springframework.stereotype.Component;

@Component
public class CcmPtsRestRequestAdapterImpl implements RestRequestAdapter<CcmPtsRequest> {

    @Override
    public AttestationMessage adapt(CcmPtsRequest requestMessage) {
        // todo
        return AttestationMessage.builder().build();
    }

}
