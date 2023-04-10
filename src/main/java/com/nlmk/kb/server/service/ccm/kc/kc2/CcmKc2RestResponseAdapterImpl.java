package com.nlmk.kb.server.service.ccm.kc.kc2;

import com.nlmk.attestation.product.api.pam.ProductAttestationResultDto;
import com.nlmk.attestation.product.api.specification.SpecCode;
import com.nlmk.kb.server.api.ccm.kc.response.*;
import com.nlmk.kb.server.api.ccm.kc.Pk;
import com.nlmk.kb.server.service.ccm.RestResponseAdapter;
import com.nlmk.kb.server.service.ccm.kc.CcmKcRestResponseAdapter;
import java.util.*;
import org.springframework.stereotype.Component;

@Component
public class CcmKc2RestResponseAdapterImpl extends CcmKcRestResponseAdapter implements RestResponseAdapter<CcmKc2Response> {

    @Override
    public CcmKc2Response adapt(ProductAttestationResultDto attResult) {
        if (attResult == null || attResult.getResult() == null) {
            return CcmKc2Response.builder().build();
        }

        final var product = attResult.getResult();

        if (product.getRequests() == null || product.getRequests().isEmpty()) {
            return CcmKc2Response.builder().build();
        }

        final var request = product.getRequests().get(0);

        return CcmKc2Response.builder()
                .ts(new Date())
                .pk(Pk.builder()
                        .id(product.getId() != null ? product.getId().toString() : null)
                        .systemCode(SpecCode.SYSTEM_CODE.getValue().toString())
                        .build())
                .data(Record.builder()
                        .primeSystemCode(product.getReferenceCode())
                        .primeId(request.getPrimeID())
                        .mismatch(Mismatch.builder()
                                .code(request.getStatus() != null ? request.getStatus().getValue() : null)
                                .name(request.getStatus() != null ? request.getStatus().getDesc() : null)
                                .build())
                        .attestationList(prepareAttestation(request))
                        .build())
                .build();
    }

}
