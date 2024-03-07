package com.nlmk.kb.server.service.result.sending.adapter;

import com.nlmk.attestation.product.api.AttestationDto;
import com.nlmk.attestation.product.api.DocId;
import com.nlmk.attestation.product.api.Group;
import com.nlmk.attestation.product.api.ProductDto;
import com.nlmk.attestation.product.api.Status;
import com.nlmk.attestation.product.api.specification.SpecCode;
import com.nlmk.kb.server.exception.AttestationResultSenderException;
import com.nlmk.kb.server.service.result.configuration.ApcsAvro;
import com.nlmk.kb.server.util.AdapterUtils;
import nlmk.l3.apcs.EnumOp;
import nlmk.l3.apcs.RecordPk;
import nlmk.l3.apcs.RecordPtsAttList;
import nlmk.l3.apcs.RecordPtsAttListMismatch;
import nlmk.l3.apcs.RecordPtsAttListNorms;
import nlmk.l3.apcs.RecordPtsAttListNormsValues;
import nlmk.l3.apcs.RecordPtsAttListParams;
import nlmk.l3.apcs.RecordPtsAttListValues;
import nlmk.l3.apcs.RecordPtsData;
import nlmk.l3.apcs.RecordPtsMismatch;
import nlmk.l3.apcs.VerificationResultsPts;
import org.apache.avro.Schema;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Service
public class PtsResultAdapter implements ApcsAvro, ResultAdapter<VerificationResultsPts> {

    private static final Schema SCHEMA = VerificationResultsPts.SCHEMA$;

    private final SimpleDateFormat dateFormatter;

    public PtsResultAdapter() {
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    public Class<VerificationResultsPts> getSendingType() {
        return VerificationResultsPts.class;
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
            return ((VerificationResultsPts) recordBase).getPk();
        } catch (Exception e) {
            throw new AttestationResultSenderException("getPk, PK сообщения не найден");
        }
    }

    @Override
    public VerificationResultsPts adapt(ProductDto product, boolean isNew) {

        checkProduct(product);

        var mismatch = Status.WAITING_FOR_DATA;
        String ts = null;

        final var request = product.getRequests().get(0);
        final var attestations = request.getAttestations();
        final var primeId = request.getPrimeID();

        if (Objects.nonNull(request.getStatus())) {
            mismatch = request.getStatus();
        }
        if (Objects.nonNull(request.getAttestationTs())) {
            ts = dateFormatter.format(request.getAttestationTs());
        }

        return VerificationResultsPts.newBuilder()
                .setTs(ts)
                .setPk(RecordPk.newBuilder()
                        .setId(request.getId())
                        .setSystemCode(SpecCode.SYSTEM_CODE.getValue().toString())
                        .build())
                .setOp(isNew ? EnumOp.I : EnumOp.U)
                .setData(RecordPtsData.newBuilder()
                        .setPrimeSystemCode(product.getReferenceCode())
                        .setPrimeId(primeId)
                        .setMismatch(RecordPtsMismatch.newBuilder()
                                .setCode(mismatch.getValue())
                                .setName(mismatch.getDesc())
                                .build())
                        .setAttestationList(prepareRecordPtsAttLists(attestations))
                        .build())
                .build();
    }

    private List<RecordPtsAttList> prepareRecordPtsAttLists(List<AttestationDto> attestationList) {
        if (CollectionUtils.isEmpty(attestationList)) {
            return List.of();
        }

        final var attestations = new ArrayList<RecordPtsAttList>();

        // объединение групп характеристик
        Arrays.stream(Group.values()).forEach(group -> {
            final var oneGroupValues = prepareAttestationValue(attestationList, group);
            if (!oneGroupValues.isEmpty()) {
                attestations.add(
                        RecordPtsAttList.newBuilder()
                                .setGroupCode(group.getCode())
                                .setGroupName(group.name())
                                .setListValues(oneGroupValues)
                                .build()
                );
            }
        });

        return attestations;
    }

    private List<RecordPtsAttListValues> prepareAttestationValue(List<AttestationDto> attResult, Group group) {
        return attResult.stream()
                .filter(attestation -> group.equals(attestation.getGroup()))
                .filter(attestation -> Objects.nonNull(attestation.getCode()))
                .map(attestation -> {
                    final var specCode = SpecCode.fromValue(attestation.getCode());
                    // пропускаем: format, measure
                    return RecordPtsAttListValues.newBuilder()
                            .setCode(specCode.getValue())
                            .setName(specCode.getDesc())
                            .setTypeCode(specCode.getTypeCode().getValue())
                            .setTypeName(specCode.getTypeCode().getDesc())
                            .setValue(attestation.getValue())
                            .setDocId(Objects.nonNull(attestation.getDocId())
                                      ? attestation.getDocId().getValue()
                                      : DocId.NOT_DEFINED.getValue())
                            .setDocName(Objects.nonNull(attestation.getDocId())
                                        ? attestation.getDocId().getDesc()
                                        : DocId.NOT_DEFINED.getDesc())
                            .setNormLimits(prepareNormLimit(attestation))
                            .setMismatch(RecordPtsAttListMismatch.newBuilder()
                                    .setCode(Objects.nonNull(attestation.getStatus())
                                             ? attestation.getStatus().getValue() : -1)
                                    .setName(
                                            Objects.nonNull(attestation.getStatus()) ? attestation.getStatus().getDesc()
                                                                                     : null)
                                    .build())
                            .setNote(AdapterUtils.detectNote(attestation))
                            .setDefectSuggestion(AdapterUtils.detectDefectSuggestion(attestation))
                            .setParameters(prepareParameters(attestation))
                            .build();
                })
                .collect(Collectors.toList());
    }

    private RecordPtsAttListNorms prepareNormLimit(AttestationDto attestation) {
        // в объекте AttestationDto ждем либо Equal, либо Min и (или) Max
        return RecordPtsAttListNorms.newBuilder()
                .setValueMin(attestation.getMin())
                .setValueMax(attestation.getMax())
                .setListAccValues(Objects.isNull(attestation.getEqual()) ? null : List.of(
                        RecordPtsAttListNormsValues.newBuilder().setValue(attestation.getEqual()).build()
                ))
                .build();
    }

    private List<RecordPtsAttListParams> prepareParameters(AttestationDto attestation) {
        final var map = AdapterUtils.prepareParameters(attestation);
        if (map.isEmpty()) {
            return List.of();
        }

        return map.entrySet().stream()
                .map(p -> RecordPtsAttListParams.newBuilder()
                        .setCode(p.getKey().getValue())
                        .setName(p.getKey().getDesc())
                        .setValue(p.getValue())
                        .setTypeCode(p.getKey().getTypeCode().getValue())
                        .setTypeName(p.getKey().getTypeCode().getDesc())
                        .build())
                .collect(Collectors.toList());
    }

}
