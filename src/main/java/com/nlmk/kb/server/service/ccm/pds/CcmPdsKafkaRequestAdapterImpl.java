package com.nlmk.kb.server.service.ccm.pds;

import com.nlmk.attestation.product.api.pam.AttestationRequest;
import com.nlmk.attestation.product.api.pam.Bundles;
import com.nlmk.attestation.product.api.pam.ChemicalSpec;
import com.nlmk.attestation.product.api.pam.DataField;
import com.nlmk.attestation.product.api.pam.DataPds;
import com.nlmk.attestation.product.api.pam.PdsTestData;
import com.nlmk.attestation.product.api.pam.PdsTestSpecification;
import com.nlmk.attestation.product.api.pam.Pk;
import com.nlmk.attestation.product.api.pam.SpecValue;
import com.nlmk.attestation.product.api.pam.Specs;
import com.nlmk.attestation.product.api.pam.Value;
import com.nlmk.kb.server.service.CommonConverter;
import com.nlmk.kb.server.service.ccm.KafkaRequestAdapter;
import com.nlmk.kb.server.util.AdapterUtils;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import nlmk.l3.ccm.pds.RecordBundles;
import nlmk.l3.ccm.pds.RecordChemical;
import nlmk.l3.ccm.pds.RecordListValues;
import nlmk.l3.ccm.pds.RecordPk;
import nlmk.l3.ccm.pds.RecordRootData;
import nlmk.l3.ccm.pds.RecordSpecifications;
import nlmk.l3.ccm.pds.RecordTestData;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

@Component
@RequiredArgsConstructor
public class CcmPdsKafkaRequestAdapterImpl implements KafkaRequestAdapter<nlmk.l3.ccm.pds.AttestationRequest> {

    private final CommonConverter converter;

    @Override
    public com.nlmk.attestation.product.api.pam.AttestationRequest adapt(
            nlmk.l3.ccm.pds.AttestationRequest requestMessagePds) {
        Assert.notNull(requestMessagePds, "requestMessagePds is null");
        Assert.notNull(requestMessagePds.getTs(), "requestMessagePds.getTs() is null");
        Assert.notNull(requestMessagePds.getOp(), "requestMessagePds.getOp() is null");

        final var dateRequest = converter.parseToDate(requestMessagePds.getTs().toString());
        Assert.notNull(dateRequest, "Не удалось получить сведения о ts в запросе на аттестацию");

        return AttestationRequest.builder()
                .value(Value.builder()
                        .ts(dateRequest)
                        .op(requestMessagePds.getOp().toString())
                        .pk(toPamPk(requestMessagePds.getPk()))
                        .data(toPamDataField(requestMessagePds.getPk(), requestMessagePds.getData()))
                        .build())
                .build();
    }

    private Pk toPamPk(RecordPk recordPk) {
        if (recordPk == null) {
            return null;
        }

        return Pk.builder()
                .systemCode(AdapterUtils.sequenceToString(recordPk.getSystemCode()))
                .id(AdapterUtils.sequenceToString(recordPk.getId()))
                .build();
    }

    private DataField toPamDataField(RecordPk recordPk, RecordRootData recordData) {
        String primeId = null;
        if (recordPk != null) {
            primeId = AdapterUtils.sequenceToString(recordPk.getId());
        }
        if (recordData == null) {
            return null;
        }

        return DataPds.builder()
                .primeId(primeId)
                .heat(recordData.getHeat())
                .hnum(recordData.getHnum())
                .tnum(recordData.getTnum())
                .roll(recordData.getRoll())
                .length(AdapterUtils.toBigDecimal(recordData.getLength()))
                .thickness(AdapterUtils.toBigDecimal(recordData.getThickness()))
                .width(AdapterUtils.toBigDecimal(recordData.getWidth()))
                .weightNet(AdapterUtils.toBigDecimal(recordData.getWeightNet()))
                .bundles(ObjectUtils.isEmpty(recordData.getBundles())
                        ? List.of()
                        : recordData.getBundles().stream()
                                .map(this::toPamBundles)
                                .collect(Collectors.toList()))
                .kceh(recordData.getWorkshopNum())
                .orderNum(recordData.getOrderNum())
                .orderPos(recordData.getOrderPos())
                .attestationPoint(recordData.getAttestationPoint())
                .specifications(
                        recordData.getSpecifications().stream()
                                .map(this::toPamSpecs)
                                .collect(Collectors.toList())
                )
                .chemical(
                        ObjectUtils.isEmpty(recordData.getChemical())
                                ? List.of()
                                : recordData.getChemical().stream()
                                        .map(this::toPamChemical)
                                        .collect(Collectors.toList()))
                .testData(
                        ObjectUtils.isEmpty(recordData.getTestData())
                                ? List.of()
                                : recordData.getTestData().stream()
                                        .map(this::toPamTestDataList)
                                        .collect(Collectors.toList()))
                .build();
    }

    private SpecValue toPamSpecValue(RecordListValues specValue) {
        return SpecValue.builder()
                .value(AdapterUtils.sequenceToString(specValue.getValue()))
                .build();
    }

    private List<SpecValue> toPamSpecValueList(RecordSpecifications specification) {
        if (specification.getListValues() == null || specification.getListValues().isEmpty()) {
            return List.of();
        }
        return specification.getListValues().stream()
                .map(this::toPamSpecValue)
                .collect(Collectors.toList());
    }

    private Specs toPamSpecs(RecordSpecifications specifications) {
        return Specs.builder()
                .specCode(specifications.getSpecCode())
                .specName(AdapterUtils.sequenceToString(specifications.getSpecName()))
                .specValue(AdapterUtils.sequenceToString(specifications.getSpecValue()))
                .specTypeCode(specifications.getSpecTypeCode())
                .specTypeName(AdapterUtils.sequenceToString(specifications.getSpecTypeName()))
                .specTypeValue(specifications.getSpecTypeValue())
                .listValues(toPamSpecValueList(specifications))
                .build();
    }

    private ChemicalSpec toPamChemical(RecordChemical chemical) {
        return ChemicalSpec.builder()
                .chemCode(chemical.getChemCode())
                .chemName(AdapterUtils.sequenceToString(chemical.getChemName()))
                .chemValue(AdapterUtils.sequenceToString(chemical.getChemValue()))
                .build();
    }

    private List<PdsTestSpecification> toPamPdsTestSpecification(RecordTestData testData) {
        if (ObjectUtils.isEmpty(testData.getData())) {
            return List.of();
        }
        return testData.getData().stream()
                .map(data ->
                        PdsTestSpecification.builder()
                                .code(data.getCode())
                                .name(AdapterUtils.sequenceToString(data.getName()))
                                .typeCode(data.getTypeCode())
                                .specTypeName(AdapterUtils.sequenceToString(data.getSpecTypeName()))
                                .value(AdapterUtils.sequenceToString(data.getValue()))
                                .build())
                .collect(Collectors.toList());
    }

    private PdsTestData toPamTestDataList(RecordTestData testData) {
        return PdsTestData.builder()
                .tnum(testData.getTnum())
                .testDate(AdapterUtils.sequenceToString(testData.getTestDate()))
                .sampleNum(testData.getSampleNum())
                .signAnalysis(testData.getSignAnalysis())
                .testTypeName(AdapterUtils.sequenceToString(testData.getTestTypeName()))
                .data(toPamPdsTestSpecification(testData))
                .build();
    }

    private Bundles toPamBundles(RecordBundles recordBundles) {
        return Bundles.builder()
                .stripId(AdapterUtils.sequenceToString(recordBundles.getStripId()))
                .strip(recordBundles.getStrip())
                .stripWidth(recordBundles.getStripWidth())
                .stripWeight(recordBundles.getStripWeight())
                .build();
    }
}