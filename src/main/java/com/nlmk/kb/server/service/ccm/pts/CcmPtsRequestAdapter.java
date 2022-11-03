package com.nlmk.kb.server.service.ccm.pts;

import com.nlmk.attestation.product.api.pam.ChemicalSpec;
import com.nlmk.attestation.product.api.pam.PtsMechanicalProperty;
import com.nlmk.attestation.product.api.pam.PtsPropertyAnalyzes;
import com.nlmk.attestation.product.api.pam.PtsPropertyAnalyzesValue;
import com.nlmk.attestation.product.api.pam.PtsPropertyValue;
import com.nlmk.attestation.product.api.pam.Specs;
import com.nlmk.kb.server.api.ccm.pts.CcmPtsRequest;
import com.nlmk.kb.server.config.AllowedCodesConfig;
import com.nlmk.kb.server.util.AdapterUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import nlmk.nlmk.l3.ccm.pts.db.attestation.request.ver1.RecordBundles;
import nlmk.nlmk.l3.ccm.pts.db.attestation.request.ver1.RecordData;
import nlmk.nlmk.l3.ccm.pts.db.attestation.request.ver1.RecordDataPropertiesListValuesAttrValue;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

/**
 * Общие методы подготовки Запроса на Аттестацию
 */
@Slf4j
public abstract class CcmPtsRequestAdapter {

    /**
     * Разделитель кодов для строк из конфигурации.
     */
    private static final String DELIMITER = ";";
    private final List<Integer> allowedMechanicalCodes;
    private final List<Integer> allowedAnalysisCodes;

    protected CcmPtsRequestAdapter(AllowedCodesConfig allowedCodesConfig) {
        log.info("CcmPtsRequestAdapter создан для allowedAnalysisCodes {}", allowedCodesConfig.getAllowedAnalysisCodes());
        log.info("CcmPtsRequestAdapter создан для allowedMechanicalCodes {}", allowedCodesConfig.getAllowedMechanicalCodes());
        if (StringUtils.isNotBlank(allowedCodesConfig.getAllowedMechanicalCodes())) {
            allowedMechanicalCodes = Arrays.stream(allowedCodesConfig.getAllowedMechanicalCodes().split(DELIMITER))
                    .map(Integer::parseInt)
                    .collect(Collectors.toUnmodifiableList());
        } else {
            allowedMechanicalCodes = List.of();
            log.warn("Не указано property request.ccm.pts.allowedMechanicalCodes");
        }
        if (StringUtils.isNotBlank(allowedCodesConfig.getAllowedAnalysisCodes())) {
            allowedAnalysisCodes = Arrays.stream(allowedCodesConfig.getAllowedAnalysisCodes().split(DELIMITER))
                    .map(Integer::parseInt)
                    .collect(Collectors.toUnmodifiableList());
        } else {
            allowedAnalysisCodes = List.of();
            log.warn("Не указано property request.ccm.pts.allowedAnalysisCodes");
        }
    }

    /**
     * Расчёт массы связки
     */
    protected Double calcBundleWeight(Object data) {
        if (data == null) {
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
    private Double calcBundleWeightForRequest(CcmPtsRequest requestMessage) {
        if (requestMessage.getData() == null) {
            return null;
        }

        if (requestMessage.getData().getBundles() == null) {
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
    private Double calcBundleWeightForRecord(RecordData recordData) {
        if (recordData.getBundles() == null) {
            return calcBundleWeight(AdapterUtils.parseFloat(recordData.getWeightNet()), List.of());
        }

        return calcBundleWeight(
                AdapterUtils.parseFloat(recordData.getWeightNet()),
                recordData.getBundles().stream()
                        .map(RecordBundles::getStripWeight)
                        .map(AdapterUtils::parseFloat)
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
    private Double calcBundleWeight(Double em, List<Double> bundles) {
        var weightEM = 0.0;
        if (em != null) {
            weightEM = em;
        }

        if (bundles == null || bundles.isEmpty()) {
            return weightEM;
        }

        final var weightBundle = bundles.stream()
                .filter(Objects::nonNull)
                .reduce(Double::sum)
                .orElse(0.0);

        // масса всех бунтов, входящих в одну связку, плюс масса ЕМ
        return weightBundle + weightEM;
    }

    /**
     * Подготовка общей спецификации
     */
    protected List<Specs> prepareSpecs(Object data) {
        if (data == null) {
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
        if (requestMessage.getData() == null
                || requestMessage.getData().getSpecifications() == null
                || requestMessage.getData().getSpecifications().isEmpty()) {
            return List.of();
        }

        final var specs = new ArrayList<Specs>();
        requestMessage.getData().getSpecifications().forEach(s -> {
            if (s.getSpecCode() != null && s.getSpecTypeValue() != null) {
                if (s.getSpecTypeValue() == CcmPtsRequest.SpecTypeValue.SIMPLE) {
                    specs.add(Specs.builder()
                            .specCode(s.getSpecCode())
                            .specName(s.getSpecName())
                            .specValue(s.getSpecValue())
                            .specTypeCode(s.getSpecTypeCode())
                            .specFormat(s.getSpecFormat())
                            .specMeasure(s.getSpecMeasure())
                            .build());
                } else if (s.getSpecTypeValue() == CcmPtsRequest.SpecTypeValue.ENUMERABLE
                        && s.getListValues() != null && !s.getListValues().isEmpty()) {
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
        if (recordData.getSpecifications() == null
                || recordData.getSpecifications().isEmpty()) {
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
                    && s.getListValues() != null && !s.getListValues().isEmpty()) {
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
        if (data == null) {
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
        if (requestMessage.getData() == null
                || requestMessage.getData().getChemical() == null
                || requestMessage.getData().getChemical().isEmpty()) {
            return List.of();
        }

        return requestMessage.getData().getChemical().stream()
                .filter(c0 -> Objects.nonNull(c0.getListValues()))
                .flatMap(c1 -> c1.getListValues().stream())
                .map(c2 -> ChemicalSpec.builder()
                        .chemCode(c2.getCode())
                        .chemName(c2.getName())
                        .chemValue(c2.getValue() != null ? c2.getValue().toString() : null)
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Подготовка спецификации по Химии для nlmk.l3.ccm.pts.RecordData
     */
    private List<ChemicalSpec> prepareChemicalSpecsForRecord(RecordData recordData) {
        if (recordData.getChemical() == null
                || recordData.getChemical().isEmpty()) {
            return List.of();
        }

        return recordData.getChemical().stream()
                .filter(c0 -> Objects.nonNull(c0.getListValues()))
                .flatMap(c1 -> c1.getListValues().stream())
                .map(c2 -> ChemicalSpec.builder()
                        .chemCode(c2.getCode())
                        .chemName(AdapterUtils.sequenceToString(c2.getName()))
                        .chemValue(c2.getValue() != null ? c2.getValue().toString() : null)
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Подготовка свойств Механики для ЦТС
     */
    protected List<PtsMechanicalProperty> prepareMechanicalProperties(Object data) {
        if (data == null) {
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
        if (requestMessage.getData() == null
                || requestMessage.getData().getProperties() == null
                || requestMessage.getData().getProperties().isEmpty()) {
            return List.of();
        }

        final var properties = new ArrayList<PtsMechanicalProperty>();

        requestMessage.getData().getProperties().forEach(p -> {

            List<PtsPropertyValue> listValues = null;
            List<PtsPropertyAnalyzes> listAnalyzes = null;

            if (!CollectionUtils.isEmpty(p.getListValues())) {
                listValues = p.getListValues().stream()
                        .filter(v -> allowMechanicalCode(v.getAttrCode()))
                        .map(v -> PtsPropertyValue.builder()
                                .attrCode(v.getAttrCode())
                                .attrType(v.getAttrType().getValue())
                                .attrValue(v.getAttrValue() == null ? List.of() : List.of(v.getAttrValue()))
                                .attrFormat(v.getAttrFormat())
                                .attrMeasure(v.getAttrMeasure())
                                .build())
                        .collect(Collectors.toUnmodifiableList());
            }
            if (!CollectionUtils.isEmpty(p.getAnalyzes())) {
                listAnalyzes = p.getAnalyzes().stream()
                        .map(a -> PtsPropertyAnalyzes.builder()
                                .samplingPlaceCode(a.getSamplingPlaceCode())
                                .samplingPlaceName(a.getSamplingPlaceName())
                                .analysisValue(a.getAnalysisValue().getValue())
                                .listValues(
                                        a.getListValues().stream()
                                                .filter(v -> allowMechanicalAnalysisCode(v.getAttrCode()))
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
            if (!CollectionUtils.isEmpty(listValues) || !CollectionUtils.isEmpty(listAnalyzes)) {
                properties.add(PtsMechanicalProperty.builder()
                                .probeCode(p.getProbeCode())
                                .probeName(p.getProbeName())
                                .testDate(p.getTestDate())
                                .typeCode(p.getTypeCode())
                                .typeName(p.getTypeName())
                        .listValues(listValues)
                        .analyzes(listAnalyzes)
                        .build());
            }
        });

        return properties;
    }

    /**
     * Подготовка свойств Механики для ЦТС для nlmk.l3.ccm.pts.RecordData
     */
    private List<PtsMechanicalProperty> prepareMechanicalPropertiesForRecord(RecordData recordData) {
        if (recordData.getProperties() == null
                || recordData.getProperties().isEmpty()) {
            return List.of();
        }

        final var properties = new ArrayList<PtsMechanicalProperty>();

        recordData.getProperties().forEach(p -> {

            List<PtsPropertyValue> listValues = null;
            List<PtsPropertyAnalyzes> listAnalyzes = null;

            if (!CollectionUtils.isEmpty(p.getListValues())) {
                listValues = p.getListValues().stream()
                        .filter(v -> allowMechanicalCode(v.getAttrCode()))
                        .map(v -> PtsPropertyValue.builder()
                                .attrCode(v.getAttrCode())
                                .attrType(v.getAttrType())
                                .attrValue(prepareAttrValueList(v.getAttrValue()))
                                .attrFormat(AdapterUtils.sequenceToString(v.getAttrFormat()))
                                .attrMeasure(AdapterUtils.sequenceToString(v.getAttrMeasure()))
                                .build())
                        .collect(Collectors.toUnmodifiableList());
            }
            if (!CollectionUtils.isEmpty(p.getAnalyzes())) {
                listAnalyzes = p.getAnalyzes().stream()
                        .map(a -> PtsPropertyAnalyzes.builder()
                                .samplingPlaceCode(a.getSamplingPlaceCode())
                                .samplingPlaceName(AdapterUtils.sequenceToString(a.getSamplingPlaceName()))
                                .analysisValue(a.getAnalysisValue())
                                .listValues(
                                        a.getListValues().stream()
                                                .filter(v -> allowMechanicalAnalysisCode(v.getAttrCode()))
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
            if (!CollectionUtils.isEmpty(listValues) || !CollectionUtils.isEmpty(listAnalyzes)) {
                properties.add(PtsMechanicalProperty.builder()
                        .probeCode(p.getProbeCode())
                        .probeName(AdapterUtils.sequenceToString(p.getProbeName()))
                        .testDate(AdapterUtils.sequenceToString(p.getTestDate()))
                        .typeCode(p.getTypeCode())
                        .typeName(AdapterUtils.sequenceToString(p.getTypeName()))
                        .listValues(listValues)
                        .analyzes(listAnalyzes)
                        .build());
            }
        });

        return properties;
    }

    /**
     * Только определенные коды для Механики
     */
    private boolean allowMechanicalCode(Integer code) {
        return code != null && allowedMechanicalCodes.contains(code);
    }

    /**
     * Только используемые в аттестации коды для Анализов
     */
    private boolean allowMechanicalAnalysisCode(Integer code) {
        return code != null && allowedAnalysisCodes.contains(code);
    }

    private List<String> prepareAttrValueList(List<RecordDataPropertiesListValuesAttrValue> listValues) {
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
