package com.nlmk.kb.server.service.ccm.pts;

import com.nlmk.attestation.product.api.pam.ChemicalSpec;
import com.nlmk.attestation.product.api.pam.PtsMechanicalProperty;
import com.nlmk.attestation.product.api.pam.PtsPropertyAnalyzes;
import com.nlmk.attestation.product.api.pam.PtsPropertyAnalyzesValue;
import com.nlmk.attestation.product.api.pam.PtsPropertyAttribute;
import com.nlmk.attestation.product.api.pam.PtsPropertyAttributeValue;
import com.nlmk.attestation.product.api.pam.PtsPropertyValue;
import com.nlmk.attestation.product.api.pam.Specs;
import com.nlmk.kb.server.api.ccm.SpecTypeValue;
import com.nlmk.kb.server.api.ccm.pts.CcmPtsRequest;
import com.nlmk.kb.server.util.AdapterUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import nlmk.nlmk.l3.ccm.pts.db.attestation.request.ver1.RecordBundles;
import nlmk.nlmk.l3.ccm.pts.db.attestation.request.ver1.RecordData;
import nlmk.nlmk.l3.ccm.pts.db.attestation.request.ver1.RecordDataPropertiesListValuesAttrValue;
import nlmk.nlmk.l3.ccm.pts.db.attestation.request.ver1.RecordProperties;
import org.springframework.util.CollectionUtils;

/**
 * Общие методы подготовки Запроса на Аттестацию
 */
@Slf4j
public abstract class CcmPtsRequestAdapter {

    /**
     * Расчёт массы связки
     */
    protected BigDecimal calcBundleWeight(Object data) {
        if (Objects.isNull(data)) {
            return null;
        }
        final var clazz = data.getClass();

        if (clazz == CcmPtsRequest.class) {
            return calcBundleWeightForRequest((CcmPtsRequest) data);
        } else if (clazz == RecordData.class) {
            return calcBundleWeightForRecord((RecordData) data);
        }

        throw new IllegalArgumentException(String.format("calcBundleWeight, class [%s] not found", clazz));
    }

    /**
     * Расчёт массы связки для CcmPtsRequest
     */
    private BigDecimal calcBundleWeightForRequest(CcmPtsRequest requestMessage) {
        if (Objects.isNull(requestMessage.getData())) {
            return null;
        }

        if (Objects.isNull(requestMessage.getData().getBundles())) {
            return calcBundleWeight(requestMessage.getData().getWeightNet(), List.of());
        }

        return calcBundleWeight(
                requestMessage.getData().getWeightNet(),
                requestMessage.getData().getBundles().stream()
                        .map(CcmPtsRequest.Bundle::getStripWeight)
                        .collect(Collectors.toList())
        );
    }

    /**
     * Расчёт массы связки для nlmk.l3.ccm.pts.RecordData
     */
    private BigDecimal calcBundleWeightForRecord(RecordData recordData) {
        if (Objects.isNull(recordData.getBundles())) {
            return calcBundleWeight(AdapterUtils.toBigDecimal(recordData.getWeightNet()), List.of());
        }

        return calcBundleWeight(
                AdapterUtils.toBigDecimal(recordData.getWeightNet()),
                recordData.getBundles().stream()
                        .map(RecordBundles::getStripWeight)
                        .map(AdapterUtils::toBigDecimal)
                        .collect(Collectors.toList())
        );
    }

    /**
     * Расчёт массы связки
     *
     * @param em      масса ЕМ
     * @param bundles масса всех бунтов
     * @return итоговая масса
     */
    private BigDecimal calcBundleWeight(BigDecimal em, List<BigDecimal> bundles) {
        BigDecimal weightEM = BigDecimal.ZERO;
        if (Objects.nonNull(em)) {
            weightEM = em;
        }

        if (CollectionUtils.isEmpty(bundles)) {
            return weightEM;
        }

        final var weightBundle = bundles.stream()
                .filter(Objects::nonNull)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        // масса всех бунтов, входящих в одну связку, плюс масса ЕМ
        return weightBundle.add(weightEM);
    }

    /**
     * Подготовка общей спецификации
     */
    protected List<Specs> prepareSpecs(Object data) {
        if (Objects.isNull(data)) {
            return List.of();
        }
        final var clazz = data.getClass();

        if (clazz == CcmPtsRequest.class) {
            return prepareSpecsForRequest((CcmPtsRequest) data);
        } else if (clazz == RecordData.class) {
            return prepareSpecsForRecord((RecordData) data);
        }

        throw new IllegalArgumentException(String.format("prepareSpecs, class [%s] not found", clazz));
    }

    /**
     * Подготовка общей спецификации для CcmPtsRequest
     */
    private List<Specs> prepareSpecsForRequest(CcmPtsRequest requestMessage) {
        if (Objects.isNull(requestMessage.getData())
                || CollectionUtils.isEmpty(requestMessage.getData().getSpecifications())) {
            return List.of();
        }

        final var specs = new ArrayList<Specs>();
        requestMessage.getData().getSpecifications().forEach(s -> {
            if (Objects.nonNull(s.getSpecCode()) && Objects.nonNull(s.getSpecTypeValue())) {
                if (s.getSpecTypeValue() == SpecTypeValue.SIMPLE) {
                    specs.add(Specs.builder()
                            .specCode(s.getSpecCode())
                            .specName(s.getSpecName())
                            .specValue(s.getSpecValue())
                            .specTypeCode(s.getSpecTypeCode())
                            .specFormat(s.getSpecFormat())
                            .specMeasure(s.getSpecMeasure())
                            .build());
                } else if (s.getSpecTypeValue() == SpecTypeValue.ENUMERABLE
                        && !CollectionUtils.isEmpty(s.getListValues())) {
                    s.getListValues().forEach(v -> specs.add(Specs.builder()
                            .specCode(s.getSpecCode())
                            .specName(s.getSpecName())
                            .specValue(v.getValue())
                            .specTypeCode(s.getSpecTypeCode())
                            .specFormat(s.getSpecFormat())
                            .specMeasure(s.getSpecMeasure())
                            .build()));
                }
            }
        });
        return specs;
    }

    /**
     * Подготовка общей спецификации для nlmk.l3.ccm.pts.RecordData
     */
    private List<Specs> prepareSpecsForRecord(RecordData recordData) {
        if (CollectionUtils.isEmpty(recordData.getSpecifications())) {
            return List.of();
        }

        final var specs = new ArrayList<Specs>();
        recordData.getSpecifications().forEach(s -> {
            if (s.getSpecTypeValue() == 1) { // 1 - простое
                specs.add(Specs.builder()
                        .specCode(s.getSpecCode())
                        .specName(AdapterUtils.sequenceToString(s.getSpecName()))
                        .specValue(AdapterUtils.sequenceToString(s.getSpecValue()))
                        .specTypeCode(s.getSpecTypeCode())
                        .specFormat(AdapterUtils.sequenceToString(s.getSpecFormat()))
                        .specMeasure(AdapterUtils.sequenceToString(s.getSpecMeasure()))
                        .build());
            } else if (s.getSpecTypeValue() == 2 // 2 - перечислимое
                    && !CollectionUtils.isEmpty(s.getListValues())) {
                s.getListValues().forEach(v -> specs.add(Specs.builder()
                        .specCode(s.getSpecCode())
                        .specName(AdapterUtils.sequenceToString(s.getSpecName()))
                        .specValue(AdapterUtils.sequenceToString(v.getValue()))
                        .specTypeCode(s.getSpecTypeCode())
                        .specFormat(AdapterUtils.sequenceToString(s.getSpecFormat()))
                        .specMeasure(AdapterUtils.sequenceToString(s.getSpecMeasure()))
                        .build()));
            }
        });
        return specs;
    }

    /**
     * Подготовка спецификации по Химии
     */
    protected List<ChemicalSpec> prepareChemicalSpecs(Object data) {
        if (Objects.isNull(data)) {
            return List.of();
        }
        final var clazz = data.getClass();

        if (clazz == CcmPtsRequest.class) {
            return prepareChemicalSpecsForRequest((CcmPtsRequest) data);
        } else if (clazz == nlmk.nlmk.l3.ccm.pts.db.attestation.request.ver1.RecordData.class) {
            return prepareChemicalSpecsForRecord((RecordData) data);
        }

        throw new IllegalArgumentException(String.format("prepareChemicalSpecs, class [%s] not found", clazz));
    }

    /**
     * Подготовка спецификации по Химии для CcmPtsRequest
     */
    private List<ChemicalSpec> prepareChemicalSpecsForRequest(CcmPtsRequest requestMessage) {
        if (Objects.isNull(requestMessage.getData())
                || CollectionUtils.isEmpty(requestMessage.getData().getChemical())) {
            return List.of();
        }

        return requestMessage.getData().getChemical().stream()
                .filter(c0 -> Objects.nonNull(c0.getListValues()))
                .flatMap(c1 -> c1.getListValues().stream())
                .map(c2 -> ChemicalSpec.builder()
                        .chemCode(c2.getCode())
                        .chemName(c2.getName())
                        .chemValue(Objects.nonNull(c2.getValue()) ? c2.getValue().toString() : null)
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Подготовка спецификации по Химии для nlmk.l3.ccm.pts.RecordData
     */
    private List<ChemicalSpec> prepareChemicalSpecsForRecord(RecordData recordData) {
        if (CollectionUtils.isEmpty(recordData.getChemical())) {
            return List.of();
        }

        return recordData.getChemical().stream()
                .filter(c0 -> Objects.nonNull(c0.getListValues()))
                .flatMap(c1 -> c1.getListValues().stream())
                .map(c2 -> ChemicalSpec.builder()
                        .chemCode(c2.getCode())
                        .chemName(AdapterUtils.sequenceToString(c2.getName()))
                        .chemValue(Objects.nonNull(c2.getValue()) ? c2.getValue().toString() : null)
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Подготовка свойств Механики для ЦТС
     */
    protected List<PtsMechanicalProperty> prepareMechanicalProperties(Object data) {
        if (Objects.isNull(data)) {
            return List.of();
        }
        final var clazz = data.getClass();

        if (clazz == CcmPtsRequest.class) {
            return prepareMechanicalPropertiesForRequest((CcmPtsRequest) data);
        } else if (clazz == nlmk.nlmk.l3.ccm.pts.db.attestation.request.ver1.RecordData.class) {
            return prepareMechanicalPropertiesForRecord((RecordData) data);
        }

        throw new IllegalArgumentException(String.format("prepareMechanicalProperties, class [%s] not found", clazz));
    }

    /**
     * Подготовка свойств Механики для ЦТС для CcmPtsRequest
     */
    private List<PtsMechanicalProperty> prepareMechanicalPropertiesForRequest(CcmPtsRequest requestMessage) {
        if (Objects.isNull(requestMessage.getData())
                || CollectionUtils.isEmpty(requestMessage.getData().getProperties())) {
            return List.of();
        }

        final var properties = new ArrayList<PtsMechanicalProperty>();

        requestMessage.getData().getProperties().forEach(p -> {

            List<PtsPropertyValue> listValues = preparePtsPropertyValue(p);
            List<PtsPropertyAnalyzes> listAnalyzes = preparePtsPropertyAnalyzes(p);
            List<PtsPropertyAttribute> listAttributes = preparePtsPropertyAttribute(p);

            addPtsMechanicalProperty(properties, p, listValues, listAnalyzes, listAttributes);
        });

        return properties;
    }

    private List<PtsPropertyValue> preparePtsPropertyValue(CcmPtsRequest.OneProperty property) {
        if (CollectionUtils.isEmpty(property.getListValues())) {
            return List.of();
        }

        return property.getListValues().stream()
                .filter(v -> Objects.nonNull(v.getAttrCode()))
                .map(v -> PtsPropertyValue.builder()
                        .attrCode(v.getAttrCode())
                        .attrType(v.getAttrType().getValue())
                        .attrValue(prepareAttrValueListForRequest(v.getAttrValue()))
                        .attrFormat(v.getAttrFormat())
                        .attrMeasure(v.getAttrMeasure())
                        .build())
                .collect(Collectors.toUnmodifiableList());
    }

    private List<PtsPropertyAnalyzes> preparePtsPropertyAnalyzes(CcmPtsRequest.OneProperty property) {
        if (CollectionUtils.isEmpty(property.getAnalyzes())) {
            return List.of();
        }

        return property.getAnalyzes().stream()
                .map(a -> PtsPropertyAnalyzes.builder()
                        .samplingPlaceCode(a.getSamplingPlaceCode())
                        .samplingPlaceName(a.getSamplingPlaceName())
                        .analysisValue(a.getAnalysisValue().getValue())
                        .listValues(
                                a.getListValues().stream()
                                        .filter(v -> Objects.nonNull(v.getAttrCode()))
                                        .map(v -> PtsPropertyAnalyzesValue.builder()
                                                .attrCode(v.getAttrCode())
                                                .attrType(v.getAttrType().getValue())
                                                .attrValue(v.getAttrValue())
                                                .attrFormat(v.getAttrFormat())
                                                .attrMeasure(v.getAttrMeasure())
                                                .build())
                                        .collect(Collectors.toUnmodifiableList())
                        )
                        .build())
                .collect(Collectors.toUnmodifiableList());
    }

    private List<PtsPropertyAttribute> preparePtsPropertyAttribute(CcmPtsRequest.OneProperty property) {
        if (CollectionUtils.isEmpty(property.getAttestationList())) {
            return List.of();
        }

        return property.getAttestationList().stream()
                .filter(a -> Objects.nonNull(a.getTypeCode()))
                .map(a -> PtsPropertyAttribute.builder()
                        .typeCode(a.getTypeCode())
                        .typeName(a.getTypeName())
                        .listValues(
                                a.getListValues().stream()
                                        .map(v -> PtsPropertyAttributeValue.builder()
                                                .attrCode(v.getAttrCode())
                                                .attrValue(v.getAttrValue().toString())
                                                .side(v.getSide().getValue())
                                                .build())
                                        .collect(Collectors.toUnmodifiableList())
                        )
                        .build())
                .collect(Collectors.toUnmodifiableList());
    }

    private void addPtsMechanicalProperty(List<PtsMechanicalProperty> properties,
                                          CcmPtsRequest.OneProperty property,
                                          List<PtsPropertyValue> listValues,
                                          List<PtsPropertyAnalyzes> listAnalyzes,
                                          List<PtsPropertyAttribute> listAttributes) {
        if (!CollectionUtils.isEmpty(listValues)
                || !CollectionUtils.isEmpty(listAnalyzes)
                || !CollectionUtils.isEmpty(listAttributes)) {
            properties.add(PtsMechanicalProperty.builder()
                    .probeCode(property.getProbeCode())
                    .probeName(property.getProbeName())
                    .testDate(property.getTestDate())
                    .typeCode(property.getTypeCode())
                    .typeName(property.getTypeName())
                    .listValues(listValues)
                    .analyzes(listAnalyzes)
                    .attestationList(listAttributes)
                    .build());
        }
    }

    /**
     * Подготовка свойств Механики для ЦТС для nlmk.l3.ccm.pts.RecordData
     */
    private List<PtsMechanicalProperty> prepareMechanicalPropertiesForRecord(RecordData recordData) {
        if (CollectionUtils.isEmpty(recordData.getProperties())) {
            return List.of();
        }

        final var properties = new ArrayList<PtsMechanicalProperty>();

        recordData.getProperties().forEach(p -> {

            List<PtsPropertyValue> listValues = preparePtsPropertyValue(p);
            List<PtsPropertyAnalyzes> listAnalyzes = preparePtsPropertyAnalyzes(p);
            List<PtsPropertyAttribute> listAttributes = preparePtsPropertyAttribute(p);

            addPtsMechanicalProperty(properties, p, listValues, listAnalyzes, listAttributes);
        });

        return properties;
    }

    private List<PtsPropertyValue> preparePtsPropertyValue(RecordProperties property) {
        if (CollectionUtils.isEmpty(property.getListValues())) {
            return List.of();
        }

        return property.getListValues().stream()
                .map(v -> PtsPropertyValue.builder()
                        .attrCode(v.getAttrCode())
                        .attrType(v.getAttrType())
                        .attrValue(prepareAttrValueListForRecord(v.getAttrValue()))
                        .attrFormat(AdapterUtils.sequenceToString(v.getAttrFormat()))
                        .attrMeasure(AdapterUtils.sequenceToString(v.getAttrMeasure()))
                        .build())
                .collect(Collectors.toUnmodifiableList());
    }

    private List<PtsPropertyAnalyzes> preparePtsPropertyAnalyzes(RecordProperties property) {
        if (CollectionUtils.isEmpty(property.getAnalyzes())) {
            return List.of();
        }

        return property.getAnalyzes().stream()
                .map(a -> PtsPropertyAnalyzes.builder()
                        .samplingPlaceCode(a.getSamplingPlaceCode())
                        .samplingPlaceName(AdapterUtils.sequenceToString(a.getSamplingPlaceName()))
                        .analysisValue(a.getAnalysisValue())
                        .listValues(
                                a.getListValues().stream()
                                        .map(v -> PtsPropertyAnalyzesValue.builder()
                                                .attrCode(v.getAttrCode())
                                                .attrType(v.getAttrType())
                                                .attrValue(AdapterUtils.sequenceToString(v.getAttrValue()))
                                                .attrFormat(AdapterUtils.sequenceToString(v.getAttrFormat()))
                                                .attrMeasure(AdapterUtils.sequenceToString(v.getAttrMeasure()))
                                                .build())
                                        .collect(Collectors.toUnmodifiableList())
                        )
                        .build())
                .collect(Collectors.toUnmodifiableList());
    }

    private List<PtsPropertyAttribute> preparePtsPropertyAttribute(RecordProperties property) {
        if (CollectionUtils.isEmpty(property.getAttestationList())) {
            return List.of();
        }

        return property.getAttestationList().stream()
                .map(a -> PtsPropertyAttribute.builder()
                        .typeCode(a.getTypeCode())
                        .typeName(AdapterUtils.sequenceToString(a.getTypeName()))
                        .listValues(
                                a.getListValues().stream()
                                        .map(v -> PtsPropertyAttributeValue.builder()
                                                .attrCode(v.getAttrCode())
                                                .attrValue(Float.toString(v.getAttrValue()))
                                                .side(v.getSide())
                                                .build())
                                        .collect(Collectors.toUnmodifiableList())
                        )
                        .build())
                .collect(Collectors.toUnmodifiableList());
    }

    private void addPtsMechanicalProperty(List<PtsMechanicalProperty> properties,
                                          RecordProperties property,
                                          List<PtsPropertyValue> listValues,
                                          List<PtsPropertyAnalyzes> listAnalyzes,
                                          List<PtsPropertyAttribute> listAttributes) {
        if (!CollectionUtils.isEmpty(listValues)
                || !CollectionUtils.isEmpty(listAnalyzes)
                || !CollectionUtils.isEmpty(listAttributes)) {
            properties.add(PtsMechanicalProperty.builder()
                    .probeCode(property.getProbeCode())
                    .probeName(AdapterUtils.sequenceToString(property.getProbeName()))
                    .testDate(AdapterUtils.sequenceToString(property.getTestDate()))
                    .typeCode(property.getTypeCode())
                    .typeName(AdapterUtils.sequenceToString(property.getTypeName()))
                    .listValues(listValues)
                    .analyzes(listAnalyzes)
                    .attestationList(listAttributes)
                    .build());
        }
    }

    private List<String> prepareAttrValueListForRequest(List<CcmPtsRequest.OnePropValueAttr> listValues) {
        if (CollectionUtils.isEmpty(listValues)) {
            return List.of();
        }
        return listValues.stream()
                .filter(Objects::nonNull)
                .map(CcmPtsRequest.OnePropValueAttr::getValue)
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableList());
    }

    private List<String> prepareAttrValueListForRecord(List<RecordDataPropertiesListValuesAttrValue> listValues) {
        if (CollectionUtils.isEmpty(listValues)) {
            return List.of();
        }

        return listValues.stream()
                .filter(Objects::nonNull)
                .map(v -> AdapterUtils.sequenceToString(v.getValue()))
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableList());
    }

}
