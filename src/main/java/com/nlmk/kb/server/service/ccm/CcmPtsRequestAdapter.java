package com.nlmk.kb.server.service.ccm;

import com.nlmk.attestation.product.api.pam.ChemicalSpec;
import com.nlmk.attestation.product.api.pam.PtsMechanicalProperty;
import com.nlmk.attestation.product.api.pam.PtsPropertyValue;
import com.nlmk.attestation.product.api.pam.Specs;
import com.nlmk.attestation.product.api.specification.SpecCode;
import com.nlmk.kb.server.api.ccm.pts.CcmPtsRequest;
import nlmk.l3.ccm.pts.RecordData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Общие методы подготовки Запроса на Аттестацию
 */
public abstract class CcmPtsRequestAdapter {

    /**
     * Расчёт массы связки
     */
    protected Double calcBundleWeight(Object data) {
        if (data == null) {
            return null;
        }
        final var clazz = data.getClass();

        if (clazz == CcmPtsRequest.class) {
            return calcBundleWeight((CcmPtsRequest) data);
        } else if (clazz == RecordData.class) {
            return calcBundleWeight((RecordData) data);
        }

        throw new IllegalArgumentException(String.format("calcBundleWeight, class [%s] not found", clazz));
    }

    /**
     * Расчёт массы связки для CcmPtsRequest
     */
    private Double calcBundleWeight(CcmPtsRequest requestMessage) {
        if (requestMessage == null
                || requestMessage.getData() == null
                || (requestMessage.getData().getWeightNet() == null
                && (requestMessage.getData().getBundles() == null || requestMessage.getData().getBundles().isEmpty()))) {
            return null;
        }

        var weightEM = 0.0;
        if (requestMessage.getData().getWeightNet() != null) {
            weightEM = requestMessage.getData().getWeightNet();
        }

        if (requestMessage.getData().getBundles() == null || requestMessage.getData().getBundles().isEmpty()) {
            return weightEM;
        }

        final var weightBundle = requestMessage.getData().getBundles().stream()
                .map(CcmPtsRequest.Bundle::getStripWeight)
                .filter(Objects::nonNull)
                .reduce(Double::sum)
                .orElse(0.0);

        // масса всех бунтов, входящих в одну связку, плюс масса ЕМ
        return weightBundle + weightEM;
    }

    /**
     * Расчёт массы связки для nlmk.l3.ccm.pts.RecordData
     */
    private Double calcBundleWeight(RecordData recordData) {
        if (recordData == null) {
            return null;
        }

        // todo
        return null;
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
            return prepareSpecs((CcmPtsRequest) data);
        } else if (clazz == RecordData.class) {
            return prepareSpecs((RecordData) data);
        }

        throw new IllegalArgumentException(String.format("prepareSpecs, class [%s] not found", clazz));
    }

    /**
     * Подготовка общей спецификации для CcmPtsRequest
     */
    private List<Specs> prepareSpecs(CcmPtsRequest requestMessage) {
        if (requestMessage == null
                || requestMessage.getData() == null
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
    private List<Specs> prepareSpecs(RecordData recordData) {
        if (recordData == null) {
            return List.of();
        }

        // todo
        return List.of();
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
            return prepareChemicalSpecs((CcmPtsRequest) data);
        } else if (clazz == nlmk.l3.ccm.pts.RecordData.class) {
            return prepareChemicalSpecs((RecordData) data);
        }

        throw new IllegalArgumentException(String.format("prepareChemicalSpecs, class [%s] not found", clazz));
    }

    /**
     * Подготовка спецификации по Химии для CcmPtsRequest
     */
    private List<ChemicalSpec> prepareChemicalSpecs(CcmPtsRequest requestMessage) {
        if (requestMessage == null
                || requestMessage.getData() == null
                || requestMessage.getData().getChemical() == null
                || requestMessage.getData().getChemical().isEmpty()) {
            return List.of();
        }

        return requestMessage.getData().getChemical().stream()
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
    private List<ChemicalSpec> prepareChemicalSpecs(RecordData recordData) {
        if (recordData == null) {
            return List.of();
        }

        // todo
        return List.of();
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
            return prepareMechanicalProperties((CcmPtsRequest) data);
        } else if (clazz == nlmk.l3.ccm.pts.RecordData.class) {
            return prepareMechanicalProperties((RecordData) data);
        }

        throw new IllegalArgumentException(String.format("prepareMechanicalProperties, class [%s] not found", clazz));
    }

    /**
     * Подготовка свойств Механики для ЦТС для CcmPtsRequest
     */
    private List<PtsMechanicalProperty> prepareMechanicalProperties(CcmPtsRequest requestMessage) {
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
    private List<PtsMechanicalProperty> prepareMechanicalProperties(RecordData recordData) {
        if (recordData == null) {
            return List.of();
        }

        // todo
        return List.of();
    }

}
