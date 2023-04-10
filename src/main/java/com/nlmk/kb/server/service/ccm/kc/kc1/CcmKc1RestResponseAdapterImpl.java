package com.nlmk.kb.server.service.ccm.kc.kc1;

import com.nlmk.attestation.product.api.pam.ProductAttestationResultDto;
import com.nlmk.attestation.product.api.specification.SpecCode;
import com.nlmk.kb.server.api.ccm.kc.response.*;
import com.nlmk.kb.server.api.ccm.kc.Pk;
import com.nlmk.kb.server.service.ccm.RestResponseAdapter;
import com.nlmk.kb.server.service.ccm.kc.CcmKcRestResponseAdapter;
import java.util.*;
import org.springframework.stereotype.Component;

@Component
public class CcmKc1RestResponseAdapterImpl extends CcmKcRestResponseAdapter implements RestResponseAdapter<CcmKc1Response> {

    @Override
    public CcmKc1Response adapt(ProductAttestationResultDto attResult) {
        if (attResult == null || attResult.getResult() == null) {
            return CcmKc1Response.builder().build();
        }

        final var product = attResult.getResult();

        if (product.getRequests() == null || product.getRequests().isEmpty()) {
            return CcmKc1Response.builder().build();
        }

        final var request = product.getRequests().get(0);

        return CcmKc1Response.builder()
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
