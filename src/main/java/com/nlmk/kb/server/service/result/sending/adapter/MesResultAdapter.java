package com.nlmk.kb.server.service.result.sending.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.attestation.product.api.AttestationDto;
import com.nlmk.attestation.product.api.ProductDto;
import com.nlmk.attestation.product.api.Status;
import com.nlmk.kb.server.exception.AttestationResultSenderException;
import com.nlmk.kb.server.repository.MesMessageSourceRepository;
import com.nlmk.kb.server.service.result.configuration.ApcsAvro;
import nlmk.apcs.verification.results.cgp.v0.AsapResponse;
import nlmk.apcs.verification.results.cgp.v0.Comparison;
import nlmk.apcs.verification.results.cgp.v0.EnumOp;
import nlmk.apcs.verification.results.cgp.v0.Norms;
import nlmk.apcs.verification.results.cgp.v0.RecordAddProperties;
import nlmk.apcs.verification.results.cgp.v0.RecordAnalyzes;
import nlmk.apcs.verification.results.cgp.v0.RecordData;
import nlmk.apcs.verification.results.cgp.v0.RecordMarking;
import nlmk.apcs.verification.results.cgp.v0.RecordMeasure;
import nlmk.apcs.verification.results.cgp.v0.RecordMetadata;
import nlmk.apcs.verification.results.cgp.v0.RecordPk;
import nlmk.apcs.verification.results.cgp.v0.RecordQualityIndicators;
import nlmk.apcs.verification.results.cgp.v0.RecordSys;
import nlmk.apcs.verification.results.cgp.v0.VerificationResultsCgp;
import nlmk.mes.cgp.asap.adapter.analysis.request.v1.AsapAnalysisRequestVer1;
import org.apache.avro.Schema;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

@Service
public class MesResultAdapter implements ApcsAvro, ResultAdapter<VerificationResultsCgp> {

    private static final Schema SCHEMA = VerificationResultsCgp.SCHEMA$;
    private final SimpleDateFormat dateFormatter;
    private final MesMessageSourceRepository sourceRepository;
    private final ObjectMapper objectMapper;


    public MesResultAdapter(MesMessageSourceRepository repository, ObjectMapper mapper) {
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        sourceRepository = repository;
        objectMapper = mapper;
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
    public nlmk.l3.apcs.RecordPk getPk(SpecificRecordBase recordBase) {
        try {
            var apcsPk = ((VerificationResultsCgp) recordBase).getPk();
            return nlmk.l3.apcs.RecordPk.newBuilder()
                    .setId(apcsPk.getId())
                    .setSystemCode("MES")
                    .build();
        } catch (Exception e) {
            throw new AttestationResultSenderException("getPk, PK сообщения не найден, ошибка: " + e.getMessage());
        }
    }

    @Override
    public VerificationResultsCgp adapt(ProductDto product, boolean isNew) {

        checkProduct(product);

        String ts = null;

        final var request = product.getRequests().get(0);
        final var attestations = request.getAttestations();
        final var primeId = request.getPrimeID();

        // В ответе MES требуется отправлять данные Sys, Metadata
        final var mesMessageSource = sourceRepository.findByRequestId(request.getId());
        AsapAnalysisRequestVer1 sourceRequest = null;
        if (mesMessageSource.isPresent()) {
            try {
                sourceRequest = objectMapper.readValue(mesMessageSource.get().getMessageSource(), AsapAnalysisRequestVer1.class);
            } catch (Exception e) {
                throw new AttestationResultSenderException("adapt, ошибка конвертации sourceRequest");
            }
        }

        if (sourceRequest == null) {
            throw new AttestationResultSenderException("adapt, sourceRequest не найден");
        }
        if (sourceRequest.getSys() == null) {
            throw new AttestationResultSenderException("adapt, sourceRequest.sys равно null");
        }
        if (sourceRequest.getMetadata() == null) {
            throw new AttestationResultSenderException("adapt, sourceRequest.metadata равно null");
        }

        if (Objects.nonNull(request.getAttestationTs())) {
            ts = dateFormatter.format(product.getRequests().get(0).getAttestationTs());
        }

        return VerificationResultsCgp.newBuilder()
                .setTs(ts)
                .setPk(RecordPk.newBuilder()
                        .setId(request.getId())
                        .build())
                .setOp(EnumOp.U)
                .setSys(RecordSys.newBuilder()
                        .setSeqID(sourceRequest.getSys().getSeqID())
                        .setTraceID(sourceRequest.getSys().getTraceID())
                        .build())
                .setMetadata(RecordMetadata.newBuilder()
                        .setKafkaKeySchemaID(sourceRequest.getMetadata().getKafkaKeySchemaID())
                        .setKafkaValueSchemaID(sourceRequest.getMetadata().getKafkaValueSchemaID())
                        .setKafkaKey(sourceRequest.getMetadata().getKafkaKey())
                        .setKafkaPartition(sourceRequest.getMetadata().getKafkaPartition())
                        .setKafkaOffset(sourceRequest.getMetadata().getKafkaOffset())
                        .setKafkaTimestamp(sourceRequest.getMetadata().getKafkaTimestamp())
                        .setKafkaTopic(sourceRequest.getMetadata().getKafkaTopic())
                        .setKafkaHeaders(sourceRequest.getMetadata().getKafkaHeaders())
                        .build())
                .setData(RecordData.newBuilder()
                        .setWorkshopId(sourceRequest.getData().getWorkshopId())
                        .setWorkshopName(sourceRequest.getData().getWorkshopName())
                        .setOrderNum(request.getOrderNum())
                        .setOrderPosition(request.getOrderPos())
                        .setMetalUnitId(primeId)
                        .setMarking(prepareMarkingList(sourceRequest))
                        .setAnalyzes(prepareAnalyzesList(sourceRequest, attestations))
                        .build())
                .build();
    }

    private List<RecordMarking> prepareMarkingList(AsapAnalysisRequestVer1 sourceRequest) {
        if (sourceRequest != null && sourceRequest.getData() != null && !sourceRequest.getData().getMarking().isEmpty()) {
            final var markings = new ArrayList<RecordMarking>();
            for (var source : sourceRequest.getData().getMarking()) {
                var marking = RecordMarking.newBuilder()
                        .setAttrCode(source.getAttrCode())
                        .setAttrId(source.getAttrId())
                        .setAttrName(source.getAttrName())
                        .setDataTypePhysical(source.getDataTypePhysical())
                        .setValue(source.getValue())
                        .build();
                markings.add(marking);
            }
            return markings;
        }
        return List.of();
    }

    private List<RecordAnalyzes> prepareAnalyzesList(AsapAnalysisRequestVer1 sourceRequest, List<AttestationDto> attestations) {
        if (sourceRequest != null && sourceRequest.getData() != null && !sourceRequest.getData().getAnalyzes().isEmpty()) {
            final var analyzes = new ArrayList<RecordAnalyzes>();
            for (var source : sourceRequest.getData().getAnalyzes()) {
                var analyze = RecordAnalyzes.newBuilder()
                        .setGroupId(source.getGroupId())
                        .setGroupName(source.getGroupName())
                        .setProtDate(source.getProtDate())
                        .setProtNum(source.getProtNum())
                        .setTestTypeRequestId(source.getTestTypeRequestId())
                        .setQualityIndicators(
                                prepareQualityIndicatorsList(source.getQualityIndicators(), attestations)
                        )
                        .build();
                analyzes.add(analyze);
            }
            return analyzes;
        }
        return List.of();
    }

    private List<nlmk.apcs.verification.results.cgp.v0.RecordQualityIndicators> prepareQualityIndicatorsList(
            List<nlmk.mes.cgp.asap.adapter.analysis.request.v1.RecordQualityIndicators> sourceIndicators,
            List<AttestationDto> attestations
    ) {
        final var indicators = new ArrayList<nlmk.apcs.verification.results.cgp.v0.RecordQualityIndicators>();
        for (var source : sourceIndicators) {
            var attestation = attestations.stream()
                    .filter(attestationDto -> attestationDto.getCode().equals(source.getAttrCode()))
                    .findFirst();
            var indicator = RecordQualityIndicators.newBuilder()
                    .setAttrId(source.getAttrId())
                    .setAttrCode(source.getAttrCode())
                    .setAttrName(source.getAttrName())
                    .setDataTypePhysical(source.getDataTypePhysical())
                    .setComparison(
                            source.getComparison() != null
                            ? Comparison.valueOf(source.getComparison().name())
                            : null
                    )
                    .setValue(source.getValue())
                    .setMeasure(mapMeasure(source.getMeasure()))
                    .setAddProperties(prepareAddPropertiesList(source.getAddProperties()))
                    .setAsapResponse(
                            attestation.map(this::prepareAsapResponse).orElse(prepareNotAttestedAsapResponse())
                    )
                    .build();
            indicators.add(indicator);
        }
        return indicators;
    }

    private AsapResponse prepareAsapResponse(AttestationDto attestation) {
        return AsapResponse.newBuilder()
                .setApcsAttestationResultCode(attestation.getStatus().getValue())
                .setNote(attestation.getComment())
                .setNorms(Norms.newBuilder()
                        .setValueMin(attestation.getMin().toString())
                        .setValueMax(attestation.getMax().toString())
                        .setListAccValues(
                                Objects.isNull(attestation.getEqual())
                                ? List.of()
                                : List.of(attestation.getEqual())
                        )
                        .build()
                )
                .build();
    }

    private AsapResponse prepareNotAttestedAsapResponse() {
        return AsapResponse.newBuilder()
                .setApcsAttestationResultCode(Status.NO_NEED_ATTESTATION.getValue())
                .setNote("Алгоритм не реализован в АСАП")
                .setNorms(Norms.newBuilder()
                        .setValueMin(null)
                        .setValueMax(null)
                        .setListAccValues(List.of())
                        .build())
                .build();
    }

    private List<RecordAddProperties> prepareAddPropertiesList(List<nlmk.mes.cgp.asap.adapter.analysis.request.v1.RecordAddProperties> addPropertiesList) {
        if (!addPropertiesList.isEmpty()) {
            final var properties = new ArrayList<RecordAddProperties>();
            for (var source : addPropertiesList) {
                var property = RecordAddProperties.newBuilder()
                        .setAttrId(source.getAttrId())
                        .setAttrCode(source.getAttrCode())
                        .setAttrName(source.getAttrName())
                        .setDataTypePhysical(source.getDataTypePhysical())
                        .setComparison(
                                source.getComparison() != null
                                ? Comparison.valueOf(source.getComparison().name())
                                : null
                        )
                        .setValue(source.getValue())
                        .setMeasure(mapMeasure(source.getMeasure()))
                        .build();
                properties.add(property);
            }
            return properties;
        }
        return List.of();
    }

    private RecordMeasure mapMeasure(nlmk.mes.cgp.asap.adapter.analysis.request.v1.RecordMeasure recordMeasure) {
        if (recordMeasure != null) {
            return RecordMeasure.newBuilder()
                    .setMeasureId(recordMeasure.getMeasureId())
                    .setMeasureName(recordMeasure.getMeasureName())
                    .build();
        }
        return null;
    }

    @Override
    public String getAvroName() {
        return getSchemaName();
    }

    @Override
    public Class<VerificationResultsCgp> getSendingType() {
        return VerificationResultsCgp.class;
    }

}
