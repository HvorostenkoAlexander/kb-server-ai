package com.nlmk.kb.server.service.result;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.attestation.product.api.AttestationDto;
import com.nlmk.attestation.product.api.DocId;
import com.nlmk.attestation.product.api.Group;
import com.nlmk.attestation.product.api.Params;
import com.nlmk.attestation.product.api.ProductDto;
import com.nlmk.attestation.product.api.RequestDto;
import com.nlmk.attestation.product.api.Status;
import com.nlmk.attestation.product.api.specification.SpecCode;
import com.nlmk.kb.server.service.result.sending.adapter.Kc1ResultAdapter;
import com.nlmk.kb.server.service.result.sending.adapter.Kc2ResultAdapter;
import com.nlmk.kb.server.service.result.sending.adapter.PgpResultAdapter;
import com.nlmk.kb.server.service.result.sending.adapter.PtsResultAdapter;
import com.nlmk.kb.server.service.result.sending.adapter.ResultAdapter;
import nlmk.l3.apcs.EnumOp;
import nlmk.l3.apcs.NormChemData;
import nlmk.l3.apcs.NormMechData;
import nlmk.l3.apcs.NormMetallData;
import nlmk.l3.apcs.NormSpecData;
import nlmk.l3.apcs.RecordChemical;
import nlmk.l3.apcs.RecordCommons;
import nlmk.l3.apcs.RecordData;
import nlmk.l3.apcs.RecordKc1AttList;
import nlmk.l3.apcs.RecordKc1AttListMismatch;
import nlmk.l3.apcs.RecordKc1AttListNorms;
import nlmk.l3.apcs.RecordKc1AttListNormsValues;
import nlmk.l3.apcs.RecordKc1AttListParams;
import nlmk.l3.apcs.RecordKc1AttListValues;
import nlmk.l3.apcs.RecordKc1Data;
import nlmk.l3.apcs.RecordKc1Mismatch;
import nlmk.l3.apcs.RecordKc2AttList;
import nlmk.l3.apcs.RecordKc2AttListMismatch;
import nlmk.l3.apcs.RecordKc2AttListNorms;
import nlmk.l3.apcs.RecordKc2AttListNormsValues;
import nlmk.l3.apcs.RecordKc2AttListParams;
import nlmk.l3.apcs.RecordKc2AttListValues;
import nlmk.l3.apcs.RecordKc2Data;
import nlmk.l3.apcs.RecordKc2Mismatch;
import nlmk.l3.apcs.RecordMechanical;
import nlmk.l3.apcs.RecordMechanicalParameter;
import nlmk.l3.apcs.RecordMechanicalSpecifications;
import nlmk.l3.apcs.RecordMettallographic;
import nlmk.l3.apcs.RecordMettallographicSpecifications;
import nlmk.l3.apcs.RecordPk;
import nlmk.l3.apcs.RecordPtsAttList;
import nlmk.l3.apcs.RecordPtsAttListMismatch;
import nlmk.l3.apcs.RecordPtsAttListNorms;
import nlmk.l3.apcs.RecordPtsAttListNormsValues;
import nlmk.l3.apcs.RecordPtsAttListParams;
import nlmk.l3.apcs.RecordPtsAttListValues;
import nlmk.l3.apcs.RecordPtsData;
import nlmk.l3.apcs.RecordPtsMismatch;
import nlmk.l3.apcs.VerificationResults;
import nlmk.l3.apcs.VerificationResultsKc1;
import nlmk.l3.apcs.VerificationResultsKc2;
import nlmk.l3.apcs.VerificationResultsPts;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ResultAdapterTest {

    private final ResultAdapter<VerificationResults> pgpAdapter = new PgpResultAdapter();
    private final ResultAdapter<VerificationResultsPts> ptsAdapter = new PtsResultAdapter();
    private final ResultAdapter<VerificationResultsKc1> kc1Adapter = new Kc1ResultAdapter();
    private final ResultAdapter<VerificationResultsKc2> kc2Adapter = new Kc2ResultAdapter();

    @Test
    void simpleVerificationProduct() throws IOException {
        // given
        ProductDto product = new ObjectMapper()
                .readValue(
                        getClass().getClassLoader().getResourceAsStream("json/att_result_product_98_20210518.json"),
                        ProductDto.class
                );
        product.setId(1000L);
        product.getRequests().get(0).setId(2000L);

        List<AttestationDto> commonAttestation = product.getRequests().get(0).getAttestations().stream()
                .filter(attestation ->
                        !(attestation.getGroup().equals(Group.HIM) ||
                                attestation.getGroup().equals(Group.MEH) ||
                                attestation.getGroup().equals(Group.MET))
                ).collect(Collectors.toList());

        List<AttestationDto> chemicalAttestation = product.getRequests().get(0).getAttestations().stream()
                .filter(attestation -> attestation.getGroup().equals(Group.HIM))
                .collect(Collectors.toList());

        List<AttestationDto> mechAttestation = product.getRequests().get(0).getAttestations().stream()
                .filter(attestation -> attestation.getGroup().equals(Group.MEH))
                .collect(Collectors.toList());

        List<AttestationDto> metallAttestation = product.getRequests().get(0).getAttestations().stream()
                .filter(attestation -> attestation.getGroup().equals(Group.MET))
                .collect(Collectors.toList());

        // when
        VerificationResults results = pgpAdapter.adapt(product, true);

        // then
        final var resultMech = results.getData().getMechanical().stream()
                .map(l -> l.getSpecifications().size()).mapToInt(i -> i).sum();
        final var resultMet = results.getData().getMetallographic().stream()
                .map(l -> l.getSpecifications().size()).mapToInt(i -> i).sum();

        assertThat(results.getData().getCommons()).hasSize(commonAttestation.size());
        assertThat(results.getData().getChemical()).hasSize(chemicalAttestation.size());
        assertThat(resultMech).isEqualTo(mechAttestation.size());
        assertThat(resultMet).isEqualTo(metallAttestation.size());
    }

    @Test
    void verifyPgpResult() {
        // given
        // when
        final var result = pgpAdapter.adapt(certifiedProduct(), false);
        // then
        assertThat(result).isEqualTo(expectedVerificationResultsPgp());
    }

    @Test
    void verifyPtsResult() {
        // given
        // when
        final var result = ptsAdapter.adapt(certifiedProduct(), false);
        // then
        assertThat(result).isEqualTo(expectedVerificationResultsPts());
    }

    @Test
    void verifyKc1Result() {
        // given
        // when
        final var result = kc1Adapter.adapt(certifiedProduct(), false);
        // then
        assertThat(result).isEqualTo(expectedVerificationResultsKc1());
    }

    @Test
    void verifyKc2Result() {
        // given
        // when
        final var result = kc2Adapter.adapt(certifiedProduct(), false);
        // then
        assertThat(result).isEqualTo(expectedVerificationResultsKc2());
    }


    /**
     * Результат Аттестации, условный, только обрабатываемые поля!
     */
    private ProductDto certifiedProduct() {
        return ProductDto.builder()
                .id(123L)
                .referenceCode("100")
                .requests(List.of(RequestDto.builder()
                        .id(321L)
                        .attestationTs(new Date(1_000_000_000L))
                        .primeID("1234567890")
                        .orderNum(1024L).orderPos(4)
                        .kceh(12)
                        .status(Status.MATCHED)
                        .attestations(List.of(
                                AttestationDto.builder().group(Group.COMMON)
                                        .code(SpecCode.EDGE_CHARACTER.getValue())
                                        .value("X").status(Status.MATCHED_MANUALLY).equal("X")
                                        .comment("Согласно требованиям заказа 2")
                                        .docId(DocId.ORDER)
                                        .build(),
                                AttestationDto.builder().group(Group.HIM)
                                        .code(SpecCode.MASS_FRACTION_H.getValue())
                                        .value("1.5").status(Status.MATCHED).max(2.0)
                                        .comment("Согласно ГОСТ 1")
                                        .docId(DocId.STANDARD_ASSORTMENT)
                                        .build(),
                                AttestationDto.builder().group(Group.MEH)
                                        .code(SpecCode.TEMPORARY_RESISTANCE.getValue())
                                        .value("100").status(Status.MATCHED).min(90.0).max(110.0)
                                        .comment("Согласно ГОСТ 2")
                                        .docId(DocId.STANDARD_PRODUCT)
                                        .params(Params.builder().signAnalysis(10).build())
                                        .build(),
                                AttestationDto.builder().group(Group.MEH)
                                        .code(SpecCode.IMPACT_WORK_1.getValue())
                                        .value("50").status(Status.MATCHED).min(40.0).max(70.0)
                                        .comment("Согласно ГОСТ 2")
                                        .docId(DocId.STANDARD_PRODUCT)
                                        .params(Params.builder().signAnalysis(11)
                                                .knctrator("V").temp("20").analysisId(3).build())
                                        .build(),
                                AttestationDto.builder().group(Group.MET)
                                        .code(SpecCode.SULPHIDES.getValue())
                                        .value("3.4").status(Status.MATCHED).min(3.0)
                                        .comment("Согласно ГОСТ 3")
                                        .docId(DocId.STANDARD_MARK)
                                        .params(Params.builder().signAnalysis(12).build())
                                        .build(),
                                AttestationDto.builder().group(Group.MET)
                                        .code(SpecCode.SILICATES.getValue())
                                        .value("2.3").status(Status.MATCHED).max(3.0)
                                        .comment("Согласно ГОСТ 3")
                                        .params(Params.builder().signAnalysis(13).build())
                                        .build()
                        )).build()
                )).build();
    }

    /**
     * Ожидаемый результат для цеха ЦГП
     */
    private VerificationResults expectedVerificationResultsPgp() {
        return VerificationResults.newBuilder()
                .setTs("1970-01-12T13:46:40.000Z")
                .setPk(RecordPk.newBuilder().setId(321L).setSystemCode("31").build())
                .setOp(EnumOp.U)
                .setData(RecordData.newBuilder()
                        .setPrimeId("1234567890").setKceh(12L).setMismatch(Status.MATCHED.getValue())
                        .setOrderNum(1024L).setOrderPos(4)
                        // TODO APCS-268 return when integration is fixed
//                        .setLineId(123L)
                        .setCommons(List.of(
                                RecordCommons.newBuilder()
                                        .setSpecCode(SpecCode.EDGE_CHARACTER.getValue())
                                        .setSpecTypeCode(SpecCode.EDGE_CHARACTER.getTypeCode().getValue())
                                        .setSpecTypeName(SpecCode.EDGE_CHARACTER.getTypeCode().getDesc())
                                        .setSpecValue("X").setMismatch(Status.MATCHED_MANUALLY.getValue())
                                        .setDefectSuggestion("Согласно требованиям заказа 2")
                                        .setNorms(NormSpecData.newBuilder()
                                                .setListAccValues(List.of("X"))
                                                .build())
                                        .build()
                        ))
                        .setChemical(List.of(
                                RecordChemical.newBuilder()
                                        .setSpecCode(SpecCode.MASS_FRACTION_H.getValue())
                                        .setSpecTypeCode(SpecCode.MASS_FRACTION_H.getTypeCode().getValue())
                                        .setSpecTypeName(SpecCode.MASS_FRACTION_H.getTypeCode().getDesc())
                                        .setSpecValue("1.5").setMismatch(Status.MATCHED.getValue())
                                        .setNote("Согласно ГОСТ 1")
                                        .setNorms(NormChemData.newBuilder()
                                                .setValueMax(2.0)
                                                .build())
                                        .build()
                        ))
                        .setMechanical(List.of(
                                RecordMechanical.newBuilder()
                                        .setSignAnalysis(10)
                                        .setSpecifications(List.of(
                                                RecordMechanicalSpecifications.newBuilder()
                                                        .setSpecCode(SpecCode.TEMPORARY_RESISTANCE.getValue())
                                                        .setSpecTypeCode(SpecCode.TEMPORARY_RESISTANCE.getTypeCode().getValue())
                                                        .setSpecTypeName(SpecCode.TEMPORARY_RESISTANCE.getTypeCode().getDesc())
                                                        .setSpecValue("100").setMismatch(Status.MATCHED.getValue())
                                                        .setNote("Согласно ГОСТ 2")
                                                        .setNorms(NormMechData.newBuilder()
                                                                .setValueMin(90.0).setValueMax(110.0)
                                                                .build())
                                                        .setParameters(List.of())
                                                        .build()
                                        )).build(),
                                RecordMechanical.newBuilder()
                                        .setSignAnalysis(11)
                                        .setSpecifications(List.of(RecordMechanicalSpecifications.newBuilder()
                                                .setSpecCode(SpecCode.IMPACT_WORK_1.getValue())
                                                .setSpecTypeCode(SpecCode.IMPACT_WORK_1.getTypeCode().getValue())
                                                .setSpecTypeName(SpecCode.IMPACT_WORK_1.getTypeCode().getDesc())
                                                .setSpecValue("50").setMismatch(Status.MATCHED.getValue())
                                                .setNote("Согласно ГОСТ 2")
                                                .setNorms(NormMechData.newBuilder()
                                                        .setValueMin(40.0).setValueMax(70.0)
                                                        .build())
                                                // порядок элементов определяет AdapterUtils.prepareParameters()
                                                .setParameters(List.of(
                                                        RecordMechanicalParameter.newBuilder()
                                                                .setCode(SpecCode.TEMPERATURE.getValue())
                                                                .setName(SpecCode.TEMPERATURE.getDesc())
                                                                .setValue("20")
                                                                .setTypeCode(SpecCode.TEMPERATURE.getTypeCode().getValue())
                                                                .setTypeName(SpecCode.TEMPERATURE.getTypeCode().getDesc())
                                                                .build(),
                                                        RecordMechanicalParameter.newBuilder()
                                                                .setCode(SpecCode.CONCENTRATOR.getValue())
                                                                .setName(SpecCode.CONCENTRATOR.getDesc())
                                                                .setValue("V")
                                                                .setTypeCode(SpecCode.CONCENTRATOR.getTypeCode().getValue())
                                                                .setTypeName(SpecCode.CONCENTRATOR.getTypeCode().getDesc())
                                                                .build(),
                                                        RecordMechanicalParameter.newBuilder()
                                                                .setCode(SpecCode.ANALYSIS_ID.getValue())
                                                                .setName(SpecCode.ANALYSIS_ID.getDesc())
                                                                .setValue("3")
                                                                .setTypeCode(SpecCode.ANALYSIS_ID.getTypeCode().getValue())
                                                                .setTypeName(SpecCode.ANALYSIS_ID.getTypeCode().getDesc())
                                                                .build()
                                                )).build()
                                        )).build()
                        ))
                        .setMetallographic(List.of(
                                RecordMettallographic.newBuilder()
                                        .setSignAnalysis(12)
                                        .setSpecifications(List.of(RecordMettallographicSpecifications.newBuilder()
                                                .setSpecCode(SpecCode.SULPHIDES.getValue())
                                                .setSpecTypeCode(SpecCode.SULPHIDES.getTypeCode().getValue())
                                                .setSpecTypeName(SpecCode.SULPHIDES.getTypeCode().getDesc())
                                                .setSpecValue("3.4")
                                                .setMismatch(Status.MATCHED.getValue())
                                                .setNorms(NormMetallData.newBuilder()
                                                        .setValueMin(3.0)
                                                        .build())
                                                .setNote("Согласно ГОСТ 3")
                                                .setParameters(List.of())
                                                .build()
                                        )).build(),
                                RecordMettallographic.newBuilder()
                                        .setSignAnalysis(13)
                                        .setSpecifications(List.of(
                                                RecordMettallographicSpecifications.newBuilder()
                                                        .setSpecCode(SpecCode.SILICATES.getValue())
                                                        .setSpecTypeCode(SpecCode.SILICATES.getTypeCode().getValue())
                                                        .setSpecTypeName(SpecCode.SILICATES.getTypeCode().getDesc())
                                                        .setSpecValue("2.3")
                                                        .setMismatch(Status.MATCHED.getValue())
                                                        .setNorms(NormMetallData.newBuilder()
                                                                .setValueMax(3.0)
                                                                .build())
                                                        .setNote("Согласно ГОСТ 3")
                                                        .setParameters(List.of())
                                                        .build()
                                        )).build()
                        )).build())
                .build();
    }

    /**
     * Ожидаемый результат для цеха ЦТС
     */
    private VerificationResultsPts expectedVerificationResultsPts() {
        return VerificationResultsPts.newBuilder()
                .setTs("1970-01-12T13:46:40.000Z")
                .setPk(RecordPk.newBuilder().setId(321L).setSystemCode("31").build())
                .setOp(EnumOp.U)
                .setData(RecordPtsData.newBuilder()
                        .setPrimeSystemCode("100")
                        .setPrimeId("1234567890")
                        .setMismatch(RecordPtsMismatch.newBuilder()
                                .setCode(Status.MATCHED.getValue())
                                .setName(Status.MATCHED.getDesc())
                                .build())
                        .setAttestationList(List.of(
                                RecordPtsAttList.newBuilder()
                                        .setGroupCode(Group.HIM.getCode())
                                        .setGroupName(Group.HIM.name())
                                        .setListValues(List.of(RecordPtsAttListValues.newBuilder()
                                                .setCode(SpecCode.MASS_FRACTION_H.getValue())
                                                .setName(SpecCode.MASS_FRACTION_H.getDesc())
                                                .setTypeCode(SpecCode.MASS_FRACTION_H.getTypeCode().getValue())
                                                .setTypeName(SpecCode.MASS_FRACTION_H.getTypeCode().getDesc())
                                                .setValue("1.5")
                                                .setDocId(DocId.STANDARD_ASSORTMENT.getValue())
                                                .setDocName(DocId.STANDARD_ASSORTMENT.getDesc())
                                                .setNormLimits(RecordPtsAttListNorms.newBuilder()
                                                        .setValueMax(2.0)
                                                        .build())
                                                .setMismatch(RecordPtsAttListMismatch.newBuilder()
                                                        .setCode(Status.MATCHED.getValue())
                                                        .setName(Status.MATCHED.getDesc())
                                                        .build())
                                                .setNote("Согласно ГОСТ 1")
                                                .setParameters(List.of())
                                                .build()
                                        )).build(),
                                RecordPtsAttList.newBuilder()
                                        .setGroupCode(Group.MEH.getCode())
                                        .setGroupName(Group.MEH.name())
                                        .setListValues(List.of(
                                                RecordPtsAttListValues.newBuilder()
                                                        .setCode(SpecCode.TEMPORARY_RESISTANCE.getValue())
                                                        .setName(SpecCode.TEMPORARY_RESISTANCE.getDesc())
                                                        .setTypeCode(SpecCode.TEMPORARY_RESISTANCE.getTypeCode().getValue())
                                                        .setTypeName(SpecCode.TEMPORARY_RESISTANCE.getTypeCode().getDesc())
                                                        .setValue("100")
                                                        .setDocId(DocId.STANDARD_PRODUCT.getValue())
                                                        .setDocName(DocId.STANDARD_PRODUCT.getDesc())
                                                        .setNormLimits(RecordPtsAttListNorms.newBuilder()
                                                                .setValueMin(90.0).setValueMax(110.0)
                                                                .build())
                                                        .setMismatch(RecordPtsAttListMismatch.newBuilder()
                                                                .setCode(Status.MATCHED.getValue())
                                                                .setName(Status.MATCHED.getDesc())
                                                                .build())
                                                        .setNote("Согласно ГОСТ 2")
                                                        .setParameters(List.of())
                                                        .build(),
                                                RecordPtsAttListValues.newBuilder()
                                                        .setCode(SpecCode.IMPACT_WORK_1.getValue())
                                                        .setName(SpecCode.IMPACT_WORK_1.getDesc())
                                                        .setTypeCode(SpecCode.IMPACT_WORK_1.getTypeCode().getValue())
                                                        .setTypeName(SpecCode.IMPACT_WORK_1.getTypeCode().getDesc())
                                                        .setValue("50")
                                                        .setDocId(DocId.STANDARD_PRODUCT.getValue())
                                                        .setDocName(DocId.STANDARD_PRODUCT.getDesc())
                                                        .setNormLimits(RecordPtsAttListNorms.newBuilder()
                                                                .setValueMin(40.0).setValueMax(70.0)
                                                                .build())
                                                        .setMismatch(RecordPtsAttListMismatch.newBuilder()
                                                                .setCode(Status.MATCHED.getValue())
                                                                .setName(Status.MATCHED.getDesc())
                                                                .build())
                                                        .setNote("Согласно ГОСТ 2")
                                                        .setParameters(List.of(
                                                                RecordPtsAttListParams.newBuilder()
                                                                        .setCode(SpecCode.TEMPERATURE.getValue())
                                                                        .setName(SpecCode.TEMPERATURE.getDesc())
                                                                        .setTypeCode(SpecCode.TEMPERATURE.getTypeCode().getValue())
                                                                        .setTypeName(SpecCode.TEMPERATURE.getTypeCode().getDesc())
                                                                        .setValue("20")
                                                                        .build(),
                                                                RecordPtsAttListParams.newBuilder()
                                                                        .setCode(SpecCode.CONCENTRATOR.getValue())
                                                                        .setName(SpecCode.CONCENTRATOR.getDesc())
                                                                        .setTypeCode(SpecCode.CONCENTRATOR.getTypeCode().getValue())
                                                                        .setTypeName(SpecCode.CONCENTRATOR.getTypeCode().getDesc())
                                                                        .setValue("V")
                                                                        .build(),
                                                                RecordPtsAttListParams.newBuilder()
                                                                        .setCode(SpecCode.ANALYSIS_ID.getValue())
                                                                        .setName(SpecCode.ANALYSIS_ID.getDesc())
                                                                        .setTypeCode(SpecCode.ANALYSIS_ID.getTypeCode().getValue())
                                                                        .setTypeName(SpecCode.ANALYSIS_ID.getTypeCode().getDesc())
                                                                        .setValue("3")
                                                                        .build()
                                                        )).build()
                                        )).build(),
                                RecordPtsAttList.newBuilder()
                                        .setGroupCode(Group.MET.getCode())
                                        .setGroupName(Group.MET.name())
                                        .setListValues(List.of(
                                                RecordPtsAttListValues.newBuilder()
                                                        .setCode(SpecCode.SULPHIDES.getValue())
                                                        .setName(SpecCode.SULPHIDES.getDesc())
                                                        .setTypeCode(SpecCode.SULPHIDES.getTypeCode().getValue())
                                                        .setTypeName(SpecCode.SULPHIDES.getTypeCode().getDesc())
                                                        .setValue("3.4")
                                                        .setDocId(DocId.STANDARD_MARK.getValue())
                                                        .setDocName(DocId.STANDARD_MARK.getDesc())
                                                        .setNormLimits(RecordPtsAttListNorms.newBuilder()
                                                                .setValueMin(3.0)
                                                                .build())
                                                        .setMismatch(RecordPtsAttListMismatch.newBuilder()
                                                                .setCode(Status.MATCHED.getValue())
                                                                .setName(Status.MATCHED.getDesc())
                                                                .build())
                                                        .setNote("Согласно ГОСТ 3")
                                                        .setParameters(List.of())
                                                        .build(),
                                                RecordPtsAttListValues.newBuilder()
                                                        .setCode(SpecCode.SILICATES.getValue())
                                                        .setName(SpecCode.SILICATES.getDesc())
                                                        .setTypeCode(SpecCode.SILICATES.getTypeCode().getValue())
                                                        .setTypeName(SpecCode.SILICATES.getTypeCode().getDesc())
                                                        .setValue("2.3")
                                                        .setDocId(DocId.NOT_DEFINED.getValue())
                                                        .setDocName(DocId.NOT_DEFINED.getDesc())
                                                        .setNormLimits(RecordPtsAttListNorms.newBuilder()
                                                                .setValueMax(3.0)
                                                                .build())
                                                        .setMismatch(RecordPtsAttListMismatch.newBuilder()
                                                                .setCode(Status.MATCHED.getValue())
                                                                .setName(Status.MATCHED.getDesc())
                                                                .build())
                                                        .setNote("Согласно ГОСТ 3")
                                                        .setParameters(List.of())
                                                        .build()
                                        )).build(),
                                RecordPtsAttList.newBuilder()
                                        .setGroupCode(Group.COMMON.getCode())
                                        .setGroupName(Group.COMMON.name())
                                        .setListValues(List.of(
                                                RecordPtsAttListValues.newBuilder()
                                                        .setCode(SpecCode.EDGE_CHARACTER.getValue())
                                                        .setName(SpecCode.EDGE_CHARACTER.getDesc())
                                                        .setTypeCode(SpecCode.EDGE_CHARACTER.getTypeCode().getValue())
                                                        .setTypeName(SpecCode.EDGE_CHARACTER.getTypeCode().getDesc())
                                                        .setValue("X")
                                                        .setDocId(DocId.ORDER.getValue())
                                                        .setDocName(DocId.ORDER.getDesc())
                                                        .setNormLimits(RecordPtsAttListNorms.newBuilder()
                                                                .setListAccValues(List.of(
                                                                        RecordPtsAttListNormsValues.newBuilder()
                                                                                .setValue("X")
                                                                                .build()
                                                                ))
                                                                .build())
                                                        .setMismatch(RecordPtsAttListMismatch.newBuilder()
                                                                .setCode(Status.MATCHED_MANUALLY.getValue())
                                                                .setName(Status.MATCHED_MANUALLY.getDesc())
                                                                .build())
                                                        .setDefectSuggestion("Согласно требованиям заказа 2")
                                                        .setParameters(List.of())
                                                        .build()
                                        )).build()
                        )).build())
                .build();
    }

    /**
     * Ожидаемый результат для цеха КЦ-1
     */
    private VerificationResultsKc1 expectedVerificationResultsKc1() {
        return VerificationResultsKc1.newBuilder()
                .setTs("1970-01-12T13:46:40.000Z")
                .setPk(RecordPk.newBuilder().setId(123L).setSystemCode("31").build())
                .setOp(EnumOp.U)
                .setData(RecordKc1Data.newBuilder()
                        .setPrimeSystemCode("100")
                        .setPrimeId("1234567890")
                        .setMismatch(RecordKc1Mismatch.newBuilder()
                                .setCode(Status.MATCHED.getValue())
                                .setName(Status.MATCHED.getDesc())
                                .build())
                        .setAttestationList(List.of(
                                RecordKc1AttList.newBuilder()
                                        .setGroupCode(Group.HIM.getCode())
                                        .setGroupName(Group.HIM.name())
                                        .setListValues(List.of(
                                                RecordKc1AttListValues.newBuilder()
                                                        .setCode(SpecCode.MASS_FRACTION_H.getValue())
                                                        .setName(SpecCode.MASS_FRACTION_H.getDesc())
                                                        .setTypeCode(SpecCode.MASS_FRACTION_H.getTypeCode().getValue())
                                                        .setTypeName(SpecCode.MASS_FRACTION_H.getTypeCode().getDesc())
                                                        .setValue("1.5")
                                                        .setDocId(DocId.STANDARD_ASSORTMENT.getValue())
                                                        .setDocName(DocId.STANDARD_ASSORTMENT.getDesc())
                                                        .setNormLimits(RecordKc1AttListNorms.newBuilder()
                                                                .setValueMax(2.0)
                                                                .build())
                                                        .setMismatch(RecordKc1AttListMismatch.newBuilder()
                                                                .setCode(Status.MATCHED.getValue())
                                                                .setName(Status.MATCHED.getDesc())
                                                                .build())
                                                        .setNote("Согласно ГОСТ 1")
                                                        .setParameters(List.of())
                                                        .build()
                                        )).build(),
                                RecordKc1AttList.newBuilder()
                                        .setGroupCode(Group.MEH.getCode())
                                        .setGroupName(Group.MEH.name())
                                        .setListValues(List.of(
                                                RecordKc1AttListValues.newBuilder()
                                                        .setCode(SpecCode.TEMPORARY_RESISTANCE.getValue())
                                                        .setName(SpecCode.TEMPORARY_RESISTANCE.getDesc())
                                                        .setTypeCode(SpecCode.TEMPORARY_RESISTANCE.getTypeCode().getValue())
                                                        .setTypeName(SpecCode.TEMPORARY_RESISTANCE.getTypeCode().getDesc())
                                                        .setValue("100")
                                                        .setDocId(DocId.STANDARD_PRODUCT.getValue())
                                                        .setDocName(DocId.STANDARD_PRODUCT.getDesc())
                                                        .setNormLimits(RecordKc1AttListNorms.newBuilder()
                                                                .setValueMin(90.0).setValueMax(110.0)
                                                                .build())
                                                        .setMismatch(RecordKc1AttListMismatch.newBuilder()
                                                                .setCode(Status.MATCHED.getValue())
                                                                .setName(Status.MATCHED.getDesc())
                                                                .build())
                                                        .setNote("Согласно ГОСТ 2")
                                                        .setParameters(List.of())
                                                        .build(),
                                                RecordKc1AttListValues.newBuilder()
                                                        .setCode(SpecCode.IMPACT_WORK_1.getValue())
                                                        .setName(SpecCode.IMPACT_WORK_1.getDesc())
                                                        .setTypeCode(SpecCode.IMPACT_WORK_1.getTypeCode().getValue())
                                                        .setTypeName(SpecCode.IMPACT_WORK_1.getTypeCode().getDesc())
                                                        .setValue("50")
                                                        .setDocId(DocId.STANDARD_PRODUCT.getValue())
                                                        .setDocName(DocId.STANDARD_PRODUCT.getDesc())
                                                        .setNormLimits(RecordKc1AttListNorms.newBuilder()
                                                                .setValueMin(40.0).setValueMax(70.0)
                                                                .build())
                                                        .setMismatch(RecordKc1AttListMismatch.newBuilder()
                                                                .setCode(Status.MATCHED.getValue())
                                                                .setName(Status.MATCHED.getDesc())
                                                                .build())
                                                        .setNote("Согласно ГОСТ 2")
                                                        .setParameters(List.of(
                                                                RecordKc1AttListParams.newBuilder()
                                                                        .setCode(SpecCode.TEMPERATURE.getValue())
                                                                        .setName(SpecCode.TEMPERATURE.getDesc())
                                                                        .setTypeCode(SpecCode.TEMPERATURE.getTypeCode().getValue())
                                                                        .setTypeName(SpecCode.TEMPERATURE.getTypeCode().getDesc())
                                                                        .setValue("20")
                                                                        .build(),
                                                                RecordKc1AttListParams.newBuilder()
                                                                        .setCode(SpecCode.CONCENTRATOR.getValue())
                                                                        .setName(SpecCode.CONCENTRATOR.getDesc())
                                                                        .setTypeCode(SpecCode.CONCENTRATOR.getTypeCode().getValue())
                                                                        .setTypeName(SpecCode.CONCENTRATOR.getTypeCode().getDesc())
                                                                        .setValue("V")
                                                                        .build(),
                                                                RecordKc1AttListParams.newBuilder()
                                                                        .setCode(SpecCode.ANALYSIS_ID.getValue())
                                                                        .setName(SpecCode.ANALYSIS_ID.getDesc())
                                                                        .setTypeCode(SpecCode.ANALYSIS_ID.getTypeCode().getValue())
                                                                        .setTypeName(SpecCode.ANALYSIS_ID.getTypeCode().getDesc())
                                                                        .setValue("3")
                                                                        .build()
                                                        )).build()
                                        )).build(),
                                RecordKc1AttList.newBuilder()
                                        .setGroupCode(Group.MET.getCode())
                                        .setGroupName(Group.MET.name())
                                        .setListValues(List.of(
                                                RecordKc1AttListValues.newBuilder()
                                                        .setCode(SpecCode.SULPHIDES.getValue())
                                                        .setName(SpecCode.SULPHIDES.getDesc())
                                                        .setTypeCode(SpecCode.SULPHIDES.getTypeCode().getValue())
                                                        .setTypeName(SpecCode.SULPHIDES.getTypeCode().getDesc())
                                                        .setValue("3.4")
                                                        .setDocId(DocId.STANDARD_MARK.getValue())
                                                        .setDocName(DocId.STANDARD_MARK.getDesc())
                                                        .setNormLimits(RecordKc1AttListNorms.newBuilder()
                                                                .setValueMin(3.0)
                                                                .build())
                                                        .setMismatch(RecordKc1AttListMismatch.newBuilder()
                                                                .setCode(Status.MATCHED.getValue())
                                                                .setName(Status.MATCHED.getDesc())
                                                                .build())
                                                        .setNote("Согласно ГОСТ 3")
                                                        .setParameters(List.of())
                                                        .build(),
                                                RecordKc1AttListValues.newBuilder()
                                                        .setCode(SpecCode.SILICATES.getValue())
                                                        .setName(SpecCode.SILICATES.getDesc())
                                                        .setTypeCode(SpecCode.SILICATES.getTypeCode().getValue())
                                                        .setTypeName(SpecCode.SILICATES.getTypeCode().getDesc())
                                                        .setValue("2.3")
                                                        .setDocId(DocId.NOT_DEFINED.getValue())
                                                        .setDocName(DocId.NOT_DEFINED.getDesc())
                                                        .setNormLimits(RecordKc1AttListNorms.newBuilder()
                                                                .setValueMax(3.0)
                                                                .build())
                                                        .setMismatch(RecordKc1AttListMismatch.newBuilder()
                                                                .setCode(Status.MATCHED.getValue())
                                                                .setName(Status.MATCHED.getDesc())
                                                                .build())
                                                        .setNote("Согласно ГОСТ 3")
                                                        .setParameters(List.of())
                                                        .build()
                                        )).build(),
                                RecordKc1AttList.newBuilder()
                                        .setGroupCode(Group.COMMON.getCode())
                                        .setGroupName(Group.COMMON.name())
                                        .setListValues(List.of(
                                                RecordKc1AttListValues.newBuilder()
                                                        .setCode(SpecCode.EDGE_CHARACTER.getValue())
                                                        .setName(SpecCode.EDGE_CHARACTER.getDesc())
                                                        .setTypeCode(SpecCode.EDGE_CHARACTER.getTypeCode().getValue())
                                                        .setTypeName(SpecCode.EDGE_CHARACTER.getTypeCode().getDesc())
                                                        .setValue("X")
                                                        .setDocId(DocId.ORDER.getValue())
                                                        .setDocName(DocId.ORDER.getDesc())
                                                        .setNormLimits(RecordKc1AttListNorms.newBuilder()
                                                                .setListAccValues(List.of(
                                                                        RecordKc1AttListNormsValues.newBuilder()
                                                                                .setValue("X")
                                                                                .build()
                                                                ))
                                                                .build())
                                                        .setMismatch(RecordKc1AttListMismatch.newBuilder()
                                                                .setCode(Status.MATCHED_MANUALLY.getValue())
                                                                .setName(Status.MATCHED_MANUALLY.getDesc())
                                                                .build())
                                                        .setDefectSuggestion("Согласно требованиям заказа 2")
                                                        .setParameters(List.of())
                                                        .build()
                                        )).build()
                        )).build())
                .build();
    }


    /**
     * Ожидаемый результат для цеха КЦ-2
     */
    private VerificationResultsKc2 expectedVerificationResultsKc2() {
        return VerificationResultsKc2.newBuilder()
                .setTs("1970-01-12T13:46:40.000Z")
                .setPk(RecordPk.newBuilder().setId(123L).setSystemCode("31").build())
                .setOp(EnumOp.U)
                .setData(RecordKc2Data.newBuilder()
                        .setPrimeSystemCode("100")
                        .setPrimeId("1234567890")
                        .setMismatch(RecordKc2Mismatch.newBuilder()
                                .setCode(Status.MATCHED.getValue())
                                .setName(Status.MATCHED.getDesc())
                                .build())
                        .setAttestationList(List.of(
                                RecordKc2AttList.newBuilder()
                                        .setGroupCode(Group.HIM.getCode())
                                        .setGroupName(Group.HIM.name())
                                        .setListValues(List.of(
                                                RecordKc2AttListValues.newBuilder()
                                                        .setCode(SpecCode.MASS_FRACTION_H.getValue())
                                                        .setName(SpecCode.MASS_FRACTION_H.getDesc())
                                                        .setTypeCode(SpecCode.MASS_FRACTION_H.getTypeCode().getValue())
                                                        .setTypeName(SpecCode.MASS_FRACTION_H.getTypeCode().getDesc())
                                                        .setValue("1.5")
                                                        .setDocId(DocId.STANDARD_ASSORTMENT.getValue())
                                                        .setDocName(DocId.STANDARD_ASSORTMENT.getDesc())
                                                        .setNormLimits(RecordKc2AttListNorms.newBuilder()
                                                                .setValueMax(2.0)
                                                                .build())
                                                        .setMismatch(RecordKc2AttListMismatch.newBuilder()
                                                                .setCode(Status.MATCHED.getValue())
                                                                .setName(Status.MATCHED.getDesc())
                                                                .build())
                                                        .setNote("Согласно ГОСТ 1")
                                                        .setParameters(List.of())
                                                        .build()
                                        )).build(),
                                RecordKc2AttList.newBuilder()
                                        .setGroupCode(Group.MEH.getCode())
                                        .setGroupName(Group.MEH.name())
                                        .setListValues(List.of(
                                                RecordKc2AttListValues.newBuilder()
                                                        .setCode(SpecCode.TEMPORARY_RESISTANCE.getValue())
                                                        .setName(SpecCode.TEMPORARY_RESISTANCE.getDesc())
                                                        .setTypeCode(SpecCode.TEMPORARY_RESISTANCE.getTypeCode().getValue())
                                                        .setTypeName(SpecCode.TEMPORARY_RESISTANCE.getTypeCode().getDesc())
                                                        .setValue("100")
                                                        .setDocId(DocId.STANDARD_PRODUCT.getValue())
                                                        .setDocName(DocId.STANDARD_PRODUCT.getDesc())
                                                        .setNormLimits(RecordKc2AttListNorms.newBuilder()
                                                                .setValueMin(90.0).setValueMax(110.0)
                                                                .build())
                                                        .setMismatch(RecordKc2AttListMismatch.newBuilder()
                                                                .setCode(Status.MATCHED.getValue())
                                                                .setName(Status.MATCHED.getDesc())
                                                                .build())
                                                        .setNote("Согласно ГОСТ 2")
                                                        .setParameters(List.of())
                                                        .build(),
                                                RecordKc2AttListValues.newBuilder()
                                                        .setCode(SpecCode.IMPACT_WORK_1.getValue())
                                                        .setName(SpecCode.IMPACT_WORK_1.getDesc())
                                                        .setTypeCode(SpecCode.IMPACT_WORK_1.getTypeCode().getValue())
                                                        .setTypeName(SpecCode.IMPACT_WORK_1.getTypeCode().getDesc())
                                                        .setValue("50")
                                                        .setDocId(DocId.STANDARD_PRODUCT.getValue())
                                                        .setDocName(DocId.STANDARD_PRODUCT.getDesc())
                                                        .setNormLimits(RecordKc2AttListNorms.newBuilder()
                                                                .setValueMin(40.0).setValueMax(70.0)
                                                                .build())
                                                        .setMismatch(RecordKc2AttListMismatch.newBuilder()
                                                                .setCode(Status.MATCHED.getValue())
                                                                .setName(Status.MATCHED.getDesc())
                                                                .build())
                                                        .setNote("Согласно ГОСТ 2")
                                                        .setParameters(List.of(
                                                                RecordKc2AttListParams.newBuilder()
                                                                        .setCode(SpecCode.TEMPERATURE.getValue())
                                                                        .setName(SpecCode.TEMPERATURE.getDesc())
                                                                        .setTypeCode(SpecCode.TEMPERATURE.getTypeCode().getValue())
                                                                        .setTypeName(SpecCode.TEMPERATURE.getTypeCode().getDesc())
                                                                        .setValue("20")
                                                                        .build(),
                                                                RecordKc2AttListParams.newBuilder()
                                                                        .setCode(SpecCode.CONCENTRATOR.getValue())
                                                                        .setName(SpecCode.CONCENTRATOR.getDesc())
                                                                        .setTypeCode(SpecCode.CONCENTRATOR.getTypeCode().getValue())
                                                                        .setTypeName(SpecCode.CONCENTRATOR.getTypeCode().getDesc())
                                                                        .setValue("V")
                                                                        .build(),
                                                                RecordKc2AttListParams.newBuilder()
                                                                        .setCode(SpecCode.ANALYSIS_ID.getValue())
                                                                        .setName(SpecCode.ANALYSIS_ID.getDesc())
                                                                        .setTypeCode(SpecCode.ANALYSIS_ID.getTypeCode().getValue())
                                                                        .setTypeName(SpecCode.ANALYSIS_ID.getTypeCode().getDesc())
                                                                        .setValue("3")
                                                                        .build()
                                                        )).build()
                                        )).build(),
                                RecordKc2AttList.newBuilder()
                                        .setGroupCode(Group.MET.getCode())
                                        .setGroupName(Group.MET.name())
                                        .setListValues(List.of(
                                                RecordKc2AttListValues.newBuilder()
                                                        .setCode(SpecCode.SULPHIDES.getValue())
                                                        .setName(SpecCode.SULPHIDES.getDesc())
                                                        .setTypeCode(SpecCode.SULPHIDES.getTypeCode().getValue())
                                                        .setTypeName(SpecCode.SULPHIDES.getTypeCode().getDesc())
                                                        .setValue("3.4")
                                                        .setDocId(DocId.STANDARD_MARK.getValue())
                                                        .setDocName(DocId.STANDARD_MARK.getDesc())
                                                        .setNormLimits(RecordKc2AttListNorms.newBuilder()
                                                                .setValueMin(3.0)
                                                                .build())
                                                        .setMismatch(RecordKc2AttListMismatch.newBuilder()
                                                                .setCode(Status.MATCHED.getValue())
                                                                .setName(Status.MATCHED.getDesc())
                                                                .build())
                                                        .setNote("Согласно ГОСТ 3")
                                                        .setParameters(List.of())
                                                        .build(),
                                                RecordKc2AttListValues.newBuilder()
                                                        .setCode(SpecCode.SILICATES.getValue())
                                                        .setName(SpecCode.SILICATES.getDesc())
                                                        .setTypeCode(SpecCode.SILICATES.getTypeCode().getValue())
                                                        .setTypeName(SpecCode.SILICATES.getTypeCode().getDesc())
                                                        .setValue("2.3")
                                                        .setDocId(DocId.NOT_DEFINED.getValue())
                                                        .setDocName(DocId.NOT_DEFINED.getDesc())
                                                        .setNormLimits(RecordKc2AttListNorms.newBuilder()
                                                                .setValueMax(3.0)
                                                                .build())
                                                        .setMismatch(RecordKc2AttListMismatch.newBuilder()
                                                                .setCode(Status.MATCHED.getValue())
                                                                .setName(Status.MATCHED.getDesc())
                                                                .build())
                                                        .setNote("Согласно ГОСТ 3")
                                                        .setParameters(List.of())
                                                        .build()
                                        )).build(),
                                RecordKc2AttList.newBuilder()
                                        .setGroupCode(Group.COMMON.getCode())
                                        .setGroupName(Group.COMMON.name())
                                        .setListValues(List.of(
                                                RecordKc2AttListValues.newBuilder()
                                                        .setCode(SpecCode.EDGE_CHARACTER.getValue())
                                                        .setName(SpecCode.EDGE_CHARACTER.getDesc())
                                                        .setTypeCode(SpecCode.EDGE_CHARACTER.getTypeCode().getValue())
                                                        .setTypeName(SpecCode.EDGE_CHARACTER.getTypeCode().getDesc())
                                                        .setValue("X")
                                                        .setDocId(DocId.ORDER.getValue())
                                                        .setDocName(DocId.ORDER.getDesc())
                                                        .setNormLimits(RecordKc2AttListNorms.newBuilder()
                                                                .setListAccValues(List.of(
                                                                        RecordKc2AttListNormsValues.newBuilder()
                                                                                .setValue("X")
                                                                                .build()
                                                                ))
                                                                .build())
                                                        .setMismatch(RecordKc2AttListMismatch.newBuilder()
                                                                .setCode(Status.MATCHED_MANUALLY.getValue())
                                                                .setName(Status.MATCHED_MANUALLY.getDesc())
                                                                .build())
                                                        .setDefectSuggestion("Согласно требованиям заказа 2")
                                                        .setParameters(List.of())
                                                        .build()
                                        )).build()
                        )).build())
                .build();
    }
}
