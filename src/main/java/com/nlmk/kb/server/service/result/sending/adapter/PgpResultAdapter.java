package com.nlmk.kb.server.service.result.sending.adapter;

import com.nlmk.attestation.product.api.AttestationDto;
import com.nlmk.attestation.product.api.Group;
import com.nlmk.attestation.product.api.ProductDto;
import com.nlmk.attestation.product.api.Status;
import com.nlmk.attestation.product.api.specification.SpecCode;
import com.nlmk.kb.server.exception.AttestationResultSenderException;
import com.nlmk.kb.server.service.result.configuration.ApcsAvro;
import com.nlmk.kb.server.util.AdapterUtils;
import nlmk.l3.apcs.EnumOp;
import nlmk.l3.apcs.NormChemData;
import nlmk.l3.apcs.NormMechData;
import nlmk.l3.apcs.NormMetallData;
import nlmk.l3.apcs.NormSpecData;
import nlmk.l3.apcs.RecordChemical;
import nlmk.l3.apcs.RecordCommons;
import nlmk.l3.apcs.RecordData;
import nlmk.l3.apcs.RecordMechanical;
import nlmk.l3.apcs.RecordMechanicalParameter;
import nlmk.l3.apcs.RecordMechanicalSpecifications;
import nlmk.l3.apcs.RecordMettallographic;
import nlmk.l3.apcs.RecordMettallographicParameter;
import nlmk.l3.apcs.RecordMettallographicSpecifications;
import nlmk.l3.apcs.RecordPk;
import nlmk.l3.apcs.VerificationResults;
import org.apache.avro.Schema;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Service
public class PgpResultAdapter implements ApcsAvro, ResultAdapter<VerificationResults> {

    private static final Schema SCHEMA = VerificationResults.SCHEMA$;

    private final SimpleDateFormat dateFormatter;

    public PgpResultAdapter() {
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    public Class<VerificationResults> getSendingType() {
        return VerificationResults.class;
    }

    @Override
    public String getAvroName() {
        return getSchemaName();
    }

    @Override
    public String getSchemaName() {
        return SCHEMA.getName();
    }

    @Override
    public String getSchemaDoc() {
        return SCHEMA.getDoc();
    }

    @Override
    public String getSchemaData() {
        return SCHEMA.toString(false);
    }

    @Override
    public RecordPk getPk(SpecificRecordBase recordBase) {
        try {
            return ((VerificationResults) recordBase).getPk();
        } catch (Exception e) {
            throw new AttestationResultSenderException("getPk, PK сообщения не найден");
        }
    }

    @Override
    public VerificationResults adapt(ProductDto product, boolean isNew) {

        checkProduct(product);

        var kceh = 0;
        var mismatch = Status.WAITING_FOR_DATA.getValue();
        String ts = null;

        final var request = product.getRequests().get(0);
        final var attestations = request.getAttestations();
        final var primeId = request.getPrimeID();

        if (Objects.nonNull(request.getKceh())) {
            kceh = request.getKceh();
        }
        if (Objects.nonNull(request.getStatus())) {
            mismatch = product.getRequests().get(0).getStatus().getValue();
        }
        if (Objects.nonNull(request.getAttestationTs())) {
            ts = dateFormatter.format(product.getRequests().get(0).getAttestationTs());
        }

        return VerificationResults.newBuilder()
                .setTs(ts)
                .setPk(RecordPk.newBuilder()
                        .setId(request.getId())
                        .setSystemCode(SpecCode.SYSTEM_CODE.getValue().toString())
                        .build())
                .setOp(isNew ? EnumOp.I : EnumOp.U)
                .setData(RecordData.newBuilder()
                        .setPrimeId(primeId)
                        .setOrderNum(request.getOrderNum())
                        .setOrderPos(request.getOrderPos())
                        .setKceh(kceh)
                        .setMismatch(mismatch)
                        .setCommons(toCommonRecordList(attestations))
                        .setChemical(toChemicalRecordList(attestations))
                        .setMechanical(toMechanicalRecordList(attestations))
                        .setMetallographic(toMettallographicRecordList(attestations))
                        .setCutTaskNum(request.getCutTaskNum())
                        .setCutTaskDate(request.getCutTaskDate())
                        .setCutTaskStrNum(request.getCutTaskStrNum())
                        // TODO APCS-268 return when integration is fixed
                        // .setLineId(product.getId())
                        .build()
                ).build();
    }

    private List<RecordCommons> toCommonRecordList(List<AttestationDto> attestations) {
        if (CollectionUtils.isEmpty(attestations)) {
            return List.of();
        }

        return attestations.stream()
                .filter(att -> !(Group.HIM.equals(att.getGroup())
                        || Group.MEH.equals(att.getGroup())
                        || Group.MET.equals(att.getGroup()))
                ).map(this::toCommonRecord)
                .collect(Collectors.toList());
    }

    private RecordCommons toCommonRecord(AttestationDto attestation) {
        final var type = AdapterUtils.getTypeCodeByCodeValue(attestation.getCode());

        return RecordCommons.newBuilder()
                .setSpecCode(attestation.getCode())
                .setSpecTypeCode(type.getValue())
                .setSpecTypeName(type.getDesc())
                .setSpecValue(attestation.getValue())
                .setMismatch(attestation.getStatus().getValue())
                .setNorms(NormSpecData.newBuilder()
                        .setListAccValues(Objects.isNull(attestation.getEqual())
                                          ? null
                                          : List.of(attestation.getEqual()))
                        .setValueMax(attestation.getMax())
                        .setValueMin(attestation.getMin())
                        .build())
                .setNote(AdapterUtils.detectNote(attestation))
                .setDefectSuggestion(AdapterUtils.detectDefectSuggestion(attestation))
                .build();
    }

    private List<RecordChemical> toChemicalRecordList(List<AttestationDto> attestations) {
        if (CollectionUtils.isEmpty(attestations)) {
            return List.of();
        }

        return attestations.stream()
                .filter(att -> Group.HIM.equals(att.getGroup()))
                // Filtering Cu/Ni out because it exists only in APCS
                .filter(att -> !SpecCode.CU_NI.getValue().equals(att.getCode()))
                .map(this::toChemicalRecord)
                .collect(Collectors.toList());
    }

    private RecordChemical toChemicalRecord(AttestationDto attestation) {
        final var type = AdapterUtils.getTypeCodeByCodeValue(attestation.getCode());

        return RecordChemical.newBuilder()
                .setSpecCode(attestation.getCode())
                .setSpecTypeCode(type.getValue())
                .setSpecTypeName(type.getDesc())
                .setSpecValue(attestation.getValue())
                .setMismatch(attestation.getStatus().getValue())
                .setNorms(NormChemData.newBuilder()
                        .setListAccValues(Objects.isNull(attestation.getEqual())
                                          ? null
                                          : List.of(attestation.getEqual()))
                        .setValueMax(attestation.getMax())
                        .setValueMin(attestation.getMin())
                        .build())
                .setNote(AdapterUtils.detectNote(attestation))
                .setDefectSuggestion(AdapterUtils.detectDefectSuggestion(attestation))
                .build();
    }

    private List<RecordMettallographic> toMettallographicRecordList(List<AttestationDto> attestations) {
        if (CollectionUtils.isEmpty(attestations)) {
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

    private List<RecordMechanical> toMechanicalRecordList(List<AttestationDto> attestations) {
        if (CollectionUtils.isEmpty(attestations)) {
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

    private RecordMettallographic toMettallographicRecord(Integer signAnalysis, List<AttestationDto> attestations) {
        return RecordMettallographic.newBuilder()
                .setSignAnalysis(signAnalysis)
                .setSpecifications(attestations.stream()
                        .map(this::toMettallographicSpecifications)
                        .collect(Collectors.toList()))
                .build();
    }

    private RecordMechanical toMechanicalRecord(Integer signAnalysis, List<AttestationDto> attestations) {
        return RecordMechanical.newBuilder()
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

    private RecordMettallographicSpecifications toMettallographicSpecifications(AttestationDto attestation) {
        final var type = AdapterUtils.getTypeCodeByCodeValue(attestation.getCode());

        return RecordMettallographicSpecifications.newBuilder()
                .setSpecCode(attestation.getCode())
                .setSpecTypeCode(type.getValue())
                .setSpecTypeName(type.getDesc())
                .setSpecValue(attestation.getValue())
                .setMismatch(attestation.getStatus().getValue())
                .setNorms(NormMetallData.newBuilder()
                        .setListAccValues(Objects.isNull(attestation.getEqual())
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

    private RecordMechanicalSpecifications toMechanicalSpecifications(AttestationDto attestation) {
        final var type = AdapterUtils.getTypeCodeByCodeValue(attestation.getCode());

        return RecordMechanicalSpecifications.newBuilder()
                .setSpecCode(attestation.getCode())
                .setSpecTypeCode(type.getValue())
                .setSpecTypeName(type.getDesc())
                .setSpecValue(attestation.getValue())
                .setMismatch(attestation.getStatus().getValue())
                .setNorms(NormMechData.newBuilder()
                        .setListAccValues(Objects.isNull(attestation.getEqual())
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
    private List<RecordMettallographicParameter> prepareMettallographicParameter(AttestationDto attestation) {
        final var map = AdapterUtils.prepareParameters(attestation);
        if (map.isEmpty()) {
            return List.of();
        }

        return map.entrySet().stream()
                .map(p -> RecordMettallographicParameter.newBuilder()
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
    private List<RecordMechanicalParameter> prepareMechanicalParameter(AttestationDto attestation) {
        final var map = AdapterUtils.prepareParameters(attestation);
        if (map.isEmpty()) {
            return List.of();
        }

        return map.entrySet().stream()
                .map(p -> RecordMechanicalParameter.newBuilder()
                        .setCode(p.getKey().getValue())
                        .setName(p.getKey().getDesc())
                        .setValue(p.getValue())
                        .setTypeCode(p.getKey().getTypeCode().getValue())
                        .setTypeName(p.getKey().getTypeCode().getDesc())
                        .build())
                .collect(Collectors.toList());
    }

}
