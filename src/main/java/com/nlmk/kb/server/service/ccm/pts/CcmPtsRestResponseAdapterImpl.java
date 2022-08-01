package com.nlmk.kb.server.service.ccm.pts;

import com.nlmk.attestation.product.api.AttestationDto;
import com.nlmk.attestation.product.api.Group;
import com.nlmk.attestation.product.api.RequestDto;
import com.nlmk.attestation.product.api.pam.ProductAttestationResultDto;
import com.nlmk.attestation.product.api.specification.SpecCode;
import com.nlmk.kb.server.api.ccm.pts.CcmPtsResponse;
import com.nlmk.kb.server.service.ccm.RestResponseAdapter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CcmPtsRestResponseAdapterImpl implements RestResponseAdapter<CcmPtsResponse> {

    @Override
    public CcmPtsResponse adapt(ProductAttestationResultDto attResult) {
        if (attResult == null || attResult.getResult() == null) {
            return CcmPtsResponse.builder().build();
        }

        final var product = attResult.getResult();

        if (product.getRequests() == null || product.getRequests().isEmpty()) {
            return CcmPtsResponse.builder().build();
        }

        final var request = product.getRequests().get(0);

        return CcmPtsResponse.builder()
                .ts(new Date())
                .pk(CcmPtsResponse.Pk.builder()
                        .id(product.getId() != null ? product.getId().toString() : null)
                        .systemCode(SpecCode.SYSTEM_CODE.getValue().toString())
                        .build())
                .data(CcmPtsResponse.Record.builder()
                        .primeSystemCode(product.getReferenceCode())
                        .primeId(request.getPrimeID())
                        .mismatch(CcmPtsResponse.Mismatch.builder()
                                .code(request.getStatus() != null ? request.getStatus().getValue() : null)
                                .name(request.getStatus() != null ? request.getStatus().getDesc() : null)
                                .build())
                        .attestationList(prepareAttestation(request))
                        .build())
                .build();
    }

    private List<CcmPtsResponse.Attestation> prepareAttestation(RequestDto request) {
        if (request == null
                || request.getAttestations() == null
                || request.getAttestations().isEmpty()) {
            return List.of();
        }

        final var attestations = new ArrayList<CcmPtsResponse.Attestation>();

        // объединение групп характеристик
        Arrays.stream(Group.values()).forEach(group -> {
            final var oneGroup = prepareAttestationValue(request.getAttestations(), group);
            if (!oneGroup.isEmpty()) {
                attestations.add(
                        CcmPtsResponse.Attestation.builder()
                                .groupCode(group.getCode())
                                .groupName(group.name())
                                .listValues(prepareAttestationValue(request.getAttestations(), group))
                                .build()
                );
            }
        });

        return attestations;
    }

    private List<CcmPtsResponse.AttestationValue> prepareAttestationValue(List<AttestationDto> attResult, Group group) {
        return attResult.stream()
                .filter(f -> group.equals(f.getGroup()))
                .map(attestation -> {
                    final var specCode = SpecCode.fromValue(attestation.getCode());
                    return CcmPtsResponse.AttestationValue.builder()
                            // skip: format, measure, defectSuggestion
                            // no data: docId, docName, parameters
                            .code(specCode.getValue())
                            .name(specCode.getDesc())
                            .typeCode(specCode.getTypeCode())
                            .typeName(specCode.getTypeCode().getDesc())
                            .value(attestation.getValue())
                            .normLimits(CcmPtsResponse.NormLimit.builder()
                                    // или диапазон
                                    .valueMin(attestation.getMin())
                                    .valueMax(attestation.getMax())
                                    // или одиночное значение
                                    .listAccValues(attestation.getEqual() == null ? null : List.of(
                                            CcmPtsResponse.AccValue.builder().value(attestation.getEqual()).build()
                                    ))
                                    .build())
                            .mismatch(CcmPtsResponse.Mismatch.builder()
                                    .code(attestation.getStatus() != null ? attestation.getStatus().getValue() : null)
                                    .name(attestation.getStatus() != null ? attestation.getStatus().getDesc() : null)
                                    .build())
                            .note(attestation.getComment())
                            .parameters(List.of())
                            .build();
                })
                .collect(Collectors.toList());
    }

}
