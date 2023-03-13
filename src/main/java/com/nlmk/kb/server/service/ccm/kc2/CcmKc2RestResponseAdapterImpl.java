package com.nlmk.kb.server.service.ccm.kc2;

import com.nlmk.attestation.product.api.AttestationDto;
import com.nlmk.attestation.product.api.Group;
import com.nlmk.attestation.product.api.RequestDto;
import com.nlmk.attestation.product.api.pam.ProductAttestationResultDto;
import com.nlmk.attestation.product.api.specification.SpecCode;
import com.nlmk.kb.server.api.ccm.kc.CcmKc2Response;
import com.nlmk.kb.server.service.ccm.RestResponseAdapter;
import com.nlmk.kb.server.util.AdapterUtils;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class CcmKc2RestResponseAdapterImpl implements RestResponseAdapter<CcmKc2Response> {

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
                .pk(CcmKc2Response.Pk.builder()
                        .id(product.getId() != null ? product.getId().toString() : null)
                        .systemCode(SpecCode.SYSTEM_CODE.getValue().toString())
                        .build())
                .data(CcmKc2Response.Record.builder()
                        .primeSystemCode(product.getReferenceCode())
                        .primeId(request.getPrimeID())
                        .mismatch(CcmKc2Response.Mismatch.builder()
                                .code(request.getStatus() != null ? request.getStatus().getValue() : null)
                                .name(request.getStatus() != null ? request.getStatus().getDesc() : null)
                                .build())
                        .attestationList(prepareAttestation(request))
                        .build())
                .build();
    }

    private List<CcmKc2Response.Attestation> prepareAttestation(RequestDto request) {
        if (request == null
                || request.getAttestations() == null
                || request.getAttestations().isEmpty()) {
            return List.of();
        }

        final var attestations = new ArrayList<CcmKc2Response.Attestation>();

        // объединение групп характеристик
        Arrays.stream(Group.values()).forEach(group -> {
            final var oneGroupValues = prepareAttestationValue(request.getAttestations(), group);
            if (!oneGroupValues.isEmpty()) {
                attestations.add(
                        CcmKc2Response.Attestation.builder()
                                .groupCode(group.getCode())
                                .groupName(group.name())
                                .listValues(oneGroupValues)
                                .build()
                );
            }
        });

        return attestations;

    }

    private  List<CcmKc2Response.AttestationValue> prepareAttestationValue(List<AttestationDto> attestations, Group group) {
        return attestations.stream()
                .filter(attestation -> group.equals(attestation.getGroup()))
                .filter(attestation -> Objects.nonNull(attestation.getCode()))
                .map(attestation -> {
                    final var specCode = SpecCode.fromValue(attestation.getCode());
                    // пропускаем: format, measure
                    return CcmKc2Response.AttestationValue.builder()
                            .code(specCode.getValue())
                            .name(specCode.getDesc())
                            .typeCode(specCode.getTypeCode())
                            .typeName(specCode.getTypeCode().getDesc())
                            .value(attestation.getValue())
                            .normLimits(prepareNormLimit(attestation))
                            .mismatch(CcmKc2Response.Mismatch.builder()
                                    .code(attestation.getStatus() != null ? attestation.getStatus().getValue() : null)
                                    .name(attestation.getStatus() != null ? attestation.getStatus().getDesc() : null)
                                    .build())
                            .note(AdapterUtils.detectNote(attestation))
                            .defectSuggestion(AdapterUtils.detectDefectSuggestion(attestation))
                            .parameters(prepareParameters(attestation))
                            .build();
                })
                .collect(Collectors.toList());
    }

    private CcmKc2Response.NormLimit prepareNormLimit(AttestationDto attestation) {
        // в объекте AttestationDto ждем либо Equal, либо Min и (или) Max
        return CcmKc2Response.NormLimit.builder()
                .valueMin(attestation.getMin())
                .valueMax(attestation.getMax())
                .listAccValues(attestation.getEqual() == null ? null : List.of(
                        CcmKc2Response.AccValue.builder().value(attestation.getEqual()).build()
                ))
                .build();
    }

    private List<CcmKc2Response.Parameter> prepareParameters(AttestationDto attestation) {
        final var map = AdapterUtils.prepareParameters(attestation);
        if (map.isEmpty()) {
            return List.of();
        }

        return map.entrySet().stream()
                .map(p -> CcmKc2Response.Parameter.builder()
                        .code(p.getKey().getValue())
                        .name(p.getKey().getDesc())
                        .value(p.getValue())
                        .typeCode(p.getKey().getTypeCode())
                        .typeName(p.getKey().getTypeCode().getDesc())
                        .build())
                .collect(Collectors.toList());
    }

}
