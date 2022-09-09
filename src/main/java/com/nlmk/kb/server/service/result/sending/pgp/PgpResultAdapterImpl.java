package com.nlmk.kb.server.service.result.sending.pgp;

import com.nlmk.attestation.product.api.*;
import com.nlmk.attestation.product.api.specification.SpecCode;
import com.nlmk.kb.server.service.result.sending.ResultAdapter;
import com.nlmk.kb.server.util.AdapterUtils;
import nlmk.l3.apcs.*;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PgpResultAdapterImpl implements ResultAdapter<VerificationResults> {

    private final SimpleDateFormat dateFormatter;

    public PgpResultAdapterImpl() {
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    public VerificationResults adapt(ProductDto product, boolean isNew) {
        Assert.notNull(product, "The product is null");
        Assert.notEmpty(product.getRequests(), "The product.getRequests() must contain elements.");
        Assert.notEmpty(product.getRequests().get(0).getAttestations(), "The product.getRequests().get(0).getAttestations() must contain elements.");

        var kceh = 0;
        var mismatch = Status.WAITING_FOR_DATA.getValue();
        String ts = null;

        final var attestations = product.getRequests().get(0).getAttestations();
        final var primeId = product.getRequests().get(0).getPrimeID();

        if (product.getRequests().get(0).getKceh() != null) {
            kceh = product.getRequests().get(0).getKceh();
        }
        if (product.getRequests().get(0).getStatus() != null) {
            mismatch = product.getRequests().get(0).getStatus().getValue();
        }
        if (product.getRequests().get(0).getAttestationTs() != null) {
            ts = dateFormatter.format(product.getRequests().get(0).getAttestationTs());
        }

        return VerificationResults.newBuilder()
                .setTs(ts)
                .setPk(RecordPk.newBuilder()
                        .setId(product.getId())
                        .setSystemCode(SpecCode.SYSTEM_CODE.getValue().toString())
                        .build())
                .setOp(isNew ? EnumOp.I : EnumOp.U)
                .setData(RecordPgpData.newBuilder()
                        .setPrimeId(primeId)
                        .setKceh(kceh)
                        .setMismatch(mismatch)
                        .setCommons(toCommonRecordList(attestations))
                        .setChemical(toChemicalRecordList(attestations))
                        .setMechanical(toMechanicalRecordList(attestations))
                        .setMetallographic(toMettallographicRecordList(attestations))
                        .build()
                ).build();
    }

    private List<RecordPgpCommons> toCommonRecordList(List<AttestationDto> attestations) {
        if (attestations == null || attestations.isEmpty()) {
            return List.of();
        }

        return attestations.stream()
                .filter(att -> !(Group.HIM.equals(att.getGroup())
                        || Group.MEH.equals(att.getGroup())
                        || Group.MET.equals(att.getGroup()))
                ).map(this::toCommonRecord)
                .collect(Collectors.toList());
    }

    private RecordPgpCommons toCommonRecord(AttestationDto attestation) {
        final var type = AdapterUtils.getTypeCodeByCodeValue(attestation.getCode());

        return RecordPgpCommons.newBuilder()
                .setSpecCode(attestation.getCode())
                .setSpecTypeCode(type.getValue())
                .setSpecTypeName(type.getDesc())
                .setSpecValue(attestation.getValue())
                .setMismatch(attestation.getStatus().getValue())
                .setNorms(RecordPgpComNorms.newBuilder()
                        .setListAccValues(attestation.getEqual() == null
                                ? null
                                : List.of(attestation.getEqual()))
                        .setValueMax(attestation.getMax())
                        .setValueMin(attestation.getMin())
                        .build())
                .setNote(AdapterUtils.detectNote(attestation))
                .setDefectSuggestion(AdapterUtils.detectDefectSuggestion(attestation))
                .build();
    }

    private List<RecordPgpChemical> toChemicalRecordList(List<AttestationDto> attestations) {
        if (attestations == null || attestations.isEmpty()) {
            return List.of();
        }

        return attestations.stream()
                .filter(att -> Group.HIM.equals(att.getGroup()))
                .map(this::toChemicalRecord)
                .collect(Collectors.toList());
    }

    private RecordPgpChemical toChemicalRecord(AttestationDto attestation) {
        final var type = AdapterUtils.getTypeCodeByCodeValue(attestation.getCode());

        return RecordPgpChemical.newBuilder()
                .setSpecCode(attestation.getCode())
                .setSpecTypeCode(type.getValue())
                .setSpecTypeName(type.getDesc())
                .setSpecValue(attestation.getValue())
                .setMismatch(attestation.getStatus().getValue())
                .setNorms(RecordPgpChemNorms.newBuilder()
                        .setListAccValues(attestation.getEqual() == null
                                ? null
                                : List.of(attestation.getEqual()))
                        .setValueMax(attestation.getMax())
                        .setValueMin(attestation.getMin())
                        .build())
                .setNote(AdapterUtils.detectNote(attestation))
                .setDefectSuggestion(AdapterUtils.detectDefectSuggestion(attestation))
                .build();
    }

    private List<RecordPgpMettallographic> toMettallographicRecordList(List<AttestationDto> attestations) {
        if (attestations == null || attestations.isEmpty()) {
            return List.of();
        }

        List<AttestationDto> mets = attestations.stream()
                .filter(att -> Group.MET.equals(att.getGroup()))
                .collect(Collectors.toList());
        if (mets.isEmpty()) {
            return List.of();
        }

        Map<Integer, List<AttestationDto>> signAnalysisGrouping = groupBySignAnalysis(mets);

        return signAnalysisGrouping.keySet().stream()
                .map(k -> toMettallographicRecord(k, signAnalysisGrouping.get(k)))
                .collect(Collectors.toList());
    }

    private List<RecordPgpMechanical> toMechanicalRecordList(List<AttestationDto> attestations) {
        if (attestations == null || attestations.isEmpty()) {
            return List.of();
        }

        List<AttestationDto> mechs = attestations.stream()
                .filter(att -> Group.MEH.equals(att.getGroup()))
                .collect(Collectors.toList());
        if (mechs.isEmpty()) {
            return List.of();
        }

        Map<Integer, List<AttestationDto>> signAnalysisGrouping = groupBySignAnalysis(mechs);

        return signAnalysisGrouping.keySet().stream()
                .map(k -> toMechanicalRecord(k, signAnalysisGrouping.get(k)))
                .collect(Collectors.toList());
    }

    private RecordPgpMettallographic toMettallographicRecord(Integer signAnalysis, List<AttestationDto> attestations) {
        return RecordPgpMettallographic.newBuilder()
                .setSignAnalysis(signAnalysis)
                .setSpecifications(attestations.stream()
                        .map(this::toMettallographicSpecifications)
                        .collect(Collectors.toList()))
                .build();
    }

    private RecordPgpMechanical toMechanicalRecord(Integer signAnalysis, List<AttestationDto> attestations) {
        return RecordPgpMechanical.newBuilder()
                .setSignAnalysis(signAnalysis)
                .setSpecifications(attestations.stream()
                        .map(this::toMechanicalSpecifications)
                        .collect(Collectors.toList()))
                .build();
    }

    private Map<Integer, List<AttestationDto>> groupBySignAnalysis(List<AttestationDto> attestations) {
        Map<Integer, List<AttestationDto>> attestationGroups = new HashMap<>();

        for (AttestationDto a : attestations) {
            Integer key = null;
            if (a.getParams() != null) {
                key = a.getParams().getSignAnalysis();
            }

            attestationGroups.computeIfAbsent(key, k -> new ArrayList<>()).add(a);
        }

        return attestationGroups;
    }

    private RecordPgpMetSpecs toMettallographicSpecifications(AttestationDto attestation) {
        final var type = AdapterUtils.getTypeCodeByCodeValue(attestation.getCode());

        return RecordPgpMetSpecs.newBuilder()
                .setSpecCode(attestation.getCode())
                .setSpecTypeCode(type.getValue())
                .setSpecTypeName(type.getDesc())
                .setSpecValue(attestation.getValue())
                .setMismatch(attestation.getStatus().getValue())
                .setNorms(RecordPgpMetNorms.newBuilder()
                        .setListAccValues(attestation.getEqual() == null
                                ? null
                                : List.of(attestation.getEqual()))
                        .setValueMax(attestation.getMax())
                        .setValueMin(attestation.getMin())
                        .build())
                .setNote(AdapterUtils.detectNote(attestation))
                .setDefectSuggestion(AdapterUtils.detectDefectSuggestion(attestation))
                .setParameters(prepareMettallographicParameter(attestation))
                .build();
    }

    private RecordPgpMechSpecs toMechanicalSpecifications(AttestationDto attestation) {
        final var type = AdapterUtils.getTypeCodeByCodeValue(attestation.getCode());

        return RecordPgpMechSpecs.newBuilder()
                .setSpecCode(attestation.getCode())
                .setSpecTypeCode(type.getValue())
                .setSpecTypeName(type.getDesc())
                .setSpecValue(attestation.getValue())
                .setMismatch(attestation.getStatus().getValue())
                .setNorms(RecordPgpMechNorms.newBuilder()
                        .setListAccValues(attestation.getEqual() == null
                                ? null
                                : List.of(attestation.getEqual()))
                        .setValueMax(attestation.getMax())
                        .setValueMin(attestation.getMin())
                        .build())
                .setNote(AdapterUtils.detectNote(attestation))
                .setDefectSuggestion(AdapterUtils.detectDefectSuggestion(attestation))
                .setParameters(prepareMechanicalParameter(attestation))
                .build();
    }

    /**
     * Преобразование значений объекта Params в список объектов RecordMettallographicParameter
     */
    private List<RecordPgpMetParams> prepareMettallographicParameter(AttestationDto attestation) {
        final var map = AdapterUtils.prepareParameters(attestation);
        if (map.isEmpty()) {
            return List.of();
        }

        return map.entrySet().stream()
                .map(p -> RecordPgpMetParams.newBuilder()
                        .setCode(p.getKey().getValue())
                        .setName(p.getKey().getDesc())
                        .setValue(p.getValue())
                        .setTypeCode(p.getKey().getTypeCode().getValue())
                        .setTypeName(p.getKey().getTypeCode().getDesc())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Преобразование значений объекта Params в список объектов RecordMechanicalParameter
     */
    private List<RecordPgpMechParams> prepareMechanicalParameter(AttestationDto attestation) {
        final var map = AdapterUtils.prepareParameters(attestation);
        if (map.isEmpty()) {
            return List.of();
        }

        return map.entrySet().stream()
                .map(p -> RecordPgpMechParams.newBuilder()
                        .setCode(p.getKey().getValue())
                        .setName(p.getKey().getDesc())
                        .setValue(p.getValue())
                        .setTypeCode(p.getKey().getTypeCode().getValue())
                        .setTypeName(p.getKey().getTypeCode().getDesc())
                        .build())
                .collect(Collectors.toList());
    }

}
