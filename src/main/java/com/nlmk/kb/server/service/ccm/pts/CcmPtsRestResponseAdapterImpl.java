package com.nlmk.kb.server.service.ccm.pts;

import com.nlmk.attestation.product.api.pam.ProductAttestationResultDto;
import com.nlmk.attestation.product.api.specification.SpecCode;
import com.nlmk.kb.server.api.ccm.pts.CcmPtsResponse;
import com.nlmk.kb.server.service.ccm.RestResponseAdapter;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class CcmPtsRestResponseAdapterImpl implements RestResponseAdapter<CcmPtsResponse> {

    @Override
    public CcmPtsResponse adapt(ProductAttestationResultDto attResult) {
        if (attResult == null || attResult.getResult() == null) {
            return CcmPtsResponse.builder().build();
        }

        final var product = attResult.getResult();

        final var builder = CcmPtsResponse.builder()
                .ts(new Date())
                .pk(CcmPtsResponse.Pk.builder()
                        .id(product.getId().toString())
                        .systemCode(SpecCode.SYSTEM_CODE.getValue().toString()).build()); // ?

        if (product.getRequests() == null || product.getRequests().isEmpty()) {
            return builder.build();
        }

        final var request = product.getRequests().get(0);

        builder.data(CcmPtsResponse.Record.builder()
                .primeSystemCode(product.getReferenceCode())
                .primeId(request.getPrimeID())
                .mismatch(CcmPtsResponse.Mismatch.builder()
                        .code(request.getStatus().getValue())
                        .name(request.getStatus().getDesc()) // ?
                        .build())
                .attestationList(List.of()) // ?
                .build());
        // todo
        return builder.build();
    }

}
