package com.nlmk.kb.server.service.result.sending.adapter;

import com.nlmk.attestation.product.api.*;
import com.nlmk.attestation.product.api.specification.SpecCode;
import com.nlmk.kb.server.exception.AttestationResultSenderException;
import com.nlmk.kb.server.service.result.configuration.ApcsAvro;
import com.nlmk.kb.server.util.AdapterUtils;
import nlmk.l3.apcs.*;
import org.apache.avro.Schema;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class Kc2ResultAdapter implements ApcsAvro, ResultAdapter<VerificationResultsKc2> {

    private static final Schema SCHEMA = VerificationResultsKc2.SCHEMA$;

    private final SimpleDateFormat dateFormatter;

    public Kc2ResultAdapter() {
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    public Class<VerificationResultsKc2> getSendingType() {
        return VerificationResultsKc2.class;
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
            return ((VerificationResultsKc2) recordBase).getPk();
        } catch (Exception e) {
            throw new AttestationResultSenderException("getPk, PK сообщения не найден");
        }
    }

    @Override
    public VerificationResultsKc2 adapt(ProductDto product, boolean isNew) {

        checkProduct(product);

        var mismatch = Status.WAITING_FOR_DATA;
        String ts = null;

        final var attestations = product.getRequests().get(0).getAttestations();
        final var primeId = product.getRequests().get(0).getPrimeID();

        if (product.getRequests().get(0).getStatus() != null) {
            mismatch = product.getRequests().get(0).getStatus();
        }
        if (product.getRequests().get(0).getAttestationTs() != null) {
            ts = dateFormatter.format(product.getRequests().get(0).getAttestationTs());
        }

        return VerificationResultsKc2.newBuilder()
                .setTs(ts)
                .setPk(RecordPk.newBuilder()
                        .setId(product.getId())
                        .setSystemCode(SpecCode.SYSTEM_CODE.getValue().toString())
                        .build())
                .setOp(isNew ? EnumOp.I : EnumOp.U)
                .setData(RecordKc2Data.newBuilder()
                        .setPrimeSystemCode(product.getReferenceCode())
                        .setPrimeId(primeId)
                        .setMismatch(RecordKc2Mismatch.newBuilder()
                                .setCode(mismatch.getValue())
                                .setName(mismatch.getDesc())
                                .build())
                        .setAttestationList(prepareRecordKc2AttLists(attestations))
                        .build())
                .build();
    }

    private List<RecordKc2AttList> prepareRecordKc2AttLists(List<AttestationDto> attestationList) {
        if (attestationList == null || attestationList.isEmpty()) {
            return List.of();
        }

        final var attestations = new ArrayList<RecordKc2AttList>();

        // объединение групп характеристик
        Arrays.stream(Group.values()).forEach(group -> {
            final var oneGroupValues = prepareAttestationValue(attestationList, group);
            if (!oneGroupValues.isEmpty()) {
                attestations.add(
                        RecordKc2AttList.newBuilder()
                                .setGroupCode(group.getCode())
                                .setGroupName(group.name())
                                .setListValues(oneGroupValues)
                                .build()
                );
            }
        });

        return attestations;
    }

    private List<RecordKc2AttListValues> prepareAttestationValue(List<AttestationDto> attResult, Group group) {
        return attResult.stream()
                .filter(attestation -> group.equals(attestation.getGroup()))
                .filter(attestation -> Objects.nonNull(attestation.getCode()))
                .map(attestation -> {
                    final var specCode = SpecCode.fromValue(attestation.getCode());
                    // пропускаем: format, measure
                    return RecordKc2AttListValues.newBuilder()
                            .setCode(specCode.getValue())
                            .setName(specCode.getDesc())
                            .setTypeCode(specCode.getTypeCode().getValue())
                            .setTypeName(specCode.getTypeCode().getDesc())
                            .setValue(attestation.getValue())
                            .setDocId(attestation.getDocId() != null
                                    ? attestation.getDocId().getValue()
                                    : DocId.NOT_DEFINED.getValue())
                            .setDocName(attestation.getDocId() != null
                                    ? attestation.getDocId().getDesc()
                                    : DocId.NOT_DEFINED.getDesc())
                            .setNormLimits(prepareNormLimit(attestation))
                            .setMismatch(RecordKc2AttListMismatch.newBuilder()
                                    .setCode(attestation.getStatus() != null ? attestation.getStatus().getValue() : -1)
                                    .setName(attestation.getStatus() != null ? attestation.getStatus().getDesc() : null)
                                    .build())
                            .setNote(AdapterUtils.detectNote(attestation))
                            .setDefectSuggestion(AdapterUtils.detectDefectSuggestion(attestation))
                            .setParameters(prepareParameters(attestation))
                            .build();
                })
                .collect(Collectors.toList());
    }

    private RecordKc2AttListNorms prepareNormLimit(AttestationDto attestation) {
        // в объекте AttestationDto ждем либо Equal, либо Min и (или) Max
        return RecordKc2AttListNorms.newBuilder()
                .setValueMin(attestation.getMin())
                .setValueMax(attestation.getMax())
                .setListAccValues(attestation.getEqual() == null ? null : List.of(
                        RecordKc2AttListNormsValues.newBuilder().setValue(attestation.getEqual()).build()
                ))
                .build();
    }

    private List<RecordKc2AttListParams> prepareParameters(AttestationDto attestation) {
        final var map = AdapterUtils.prepareParameters(attestation);
        if (map.isEmpty()) {
            return List.of();
        }

        return map.entrySet().stream()
                .map(p -> RecordKc2AttListParams.newBuilder()
                        .setCode(p.getKey().getValue())
                        .setName(p.getKey().getDesc())
                        .setValue(p.getValue())
                        .setTypeCode(p.getKey().getTypeCode().getValue())
                        .setTypeName(p.getKey().getTypeCode().getDesc())
                        .build())
                .collect(Collectors.toList());
    }

}
