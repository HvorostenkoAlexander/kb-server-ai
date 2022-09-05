package com.nlmk.kb.server.service.ccm;

import com.nlmk.attestation.product.api.pam.ChemicalSpec;
import com.nlmk.attestation.product.api.pam.PtsMechanicalProperty;
import com.nlmk.attestation.product.api.pam.PtsPropertyValue;
import com.nlmk.attestation.product.api.pam.Specs;
import com.nlmk.attestation.product.api.specification.SpecCode;
import com.nlmk.kb.server.api.ccm.pts.CcmPtsRequest;
import nlmk.l3.ccm.pts.RecordBundles;
import nlmk.l3.ccm.pts.RecordData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Общие методы подготовки Запроса на Аттестацию
 */
public abstract class CcmPtsRequestAdapter extends CcmRequestAdapter {

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
            return calcBundleWeight(super.parseFloat(recordData.getWeightNet()), List.of());
        }

        return calcBundleWeight(
                super.parseFloat(recordData.getWeightNet()),
                recordData.getBundles().stream()
                        .map(RecordBundles::getStripWeight)
                        .map(super::parseFloat)
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
                        .specName(sequenceToString(s.getSpecName()))
                        .specValue(sequenceToString(s.getSpecValue()))
                        .specTypeCode(s.getSpecTypeCode())
                        .specFormat(sequenceToString(s.getSpecFormat()))
                        .specMeasure(sequenceToString(s.getSpecMeasure()))
                        .build());
            } else if (s.getSpecTypeValue() == 2 // 2 - перечислимое
                    && s.getListValues() != null && !s.getListValues().isEmpty()) {
                s.getListValues().forEach(v -> specs.add(Specs.builder()
                        .specCode(s.getSpecCode())
                        .specName(sequenceToString(s.getSpecName()))
                        .specValue(sequenceToString(v.getValue()))
                        .specTypeCode(s.getSpecTypeCode())
                        .specFormat(sequenceToString(s.getSpecFormat()))
                        .specMeasure(sequenceToString(s.getSpecMeasure()))
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
        } else if (clazz == nlmk.l3.ccm.pts.RecordData.class) {
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
                        .chemName(sequenceToString(c2.getName()))
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
        } else if (clazz == nlmk.l3.ccm.pts.RecordData.class) {
            return prepareMechanicalPropertiesForRecord((RecordData) data);
        }

        throw new IllegalArgumentException(String.format("prepareMechanicalProperties, class [%s] not found", clazz));
    }

    /**
     * Подготовка свойств Механики для ЦТС для CcmPtsRequest
     */
    private List<PtsMechanicalProperty> prepareMechanicalPropertiesForRequest(CcmPtsRequest requestMessage) {
        if (requestMessage == null
                || requestMessage.getData() == null
                || requestMessage.getData().getProperties() == null
                || requestMessage.getData().getProperties().isEmpty()) {
            return List.of();
        }

        final var properties = new ArrayList<PtsMechanicalProperty>();

        requestMessage.getData().getProperties().forEach(p -> {
            if (p.getListValues() != null && !p.getListValues().isEmpty()) {
                final var oneProperty = new PtsMechanicalProperty();
                p.getListValues().forEach(v -> {
                    if (v.getAttrCode() != null) {
                        // только определенные коды для Механики
                        if (SpecCode.PLASTICITY_NUMBER_OF_BENDS.getValue().equals(v.getAttrCode())) {
                            oneProperty.setListValues(List.of(
                                    PtsPropertyValue.builder()
                                            .attrCode(v.getAttrCode())
                                            .attrType(v.getAttrType().getValue())
                                            .attrValue(v.getAttrValue())
                                            .attrFormat(v.getAttrFormat())
                                            .attrMeasure(v.getAttrMeasure())
                                            .build()
                            ));
                        }
                    }
                });
                properties.add(oneProperty);
            }
        });

        return properties;
    }

    /**
     * Подготовка свойств Механики для ЦТС для nlmk.l3.ccm.pts.RecordData
     */
    private List<PtsMechanicalProperty> prepareMechanicalPropertiesForRecord(RecordData recordData) {
        if (recordData == null) {
            return List.of();
        }

        // todo
        return List.of();
    }

}
