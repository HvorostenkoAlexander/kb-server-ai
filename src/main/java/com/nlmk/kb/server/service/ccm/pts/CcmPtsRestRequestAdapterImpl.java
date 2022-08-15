package com.nlmk.kb.server.service.ccm.pts;

import com.nlmk.attestation.product.api.pam.*;
import com.nlmk.attestation.product.api.specification.SpecCode;
import com.nlmk.kb.server.api.ccm.pts.CcmPtsRequest;
import com.nlmk.kb.server.service.CommonConverter;
import com.nlmk.kb.server.service.ccm.RestRequestAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CcmPtsRestRequestAdapterImpl implements RestRequestAdapter<CcmPtsRequest> {

    private final CommonConverter converter;

    @Override
    public AttestationRequest adapt(CcmPtsRequest requestMessage) {
        final var dateRequest = converter.parseToDate(requestMessage.getTs());
        final var data = requestMessage.getData();

        // с версии 1.27.0 данные поля orderReq не используются, получение требований заказа через SAP
        // пустые списки для mechanical, metallographic
        return AttestationRequest.builder()
                .value(Value.builder()
                        .ts(dateRequest)
                        .op("I")
                        .pk(new Pk(requestMessage.getPk().getId(), requestMessage.getPk().getSystemCode()))
                        .data(DataField.builder()
                                .primeId(requestMessage.getPk().getId())
                                .nplv(data.getMarking().getNplv())
                                .hnum(data.getMarking().getHnum())
                                .roll(data.getMarking().getRoll().toString())
                                .length(data.getGeometry().getLength())
                                .thickness(data.getGeometry().getThickness())
                                .width(data.getGeometry().getWidth())
                                .weightNet(data.getWeightNet())
                                .bundleWeight(calcBundleWeight(requestMessage))
                                .kceh(data.getKceh())
                                .orderNum(data.getOrderNum())
                                .orderPos(data.getOrderPos())
                                .orderReq(List.of())
                                .specifications(prepareSpecs(requestMessage))
                                .chemical(prepareChemicalSpecs(requestMessage))
                                .mechanicalPts(prepareMechanicalProperties(requestMessage))
                                .mechanical(List.of())
                                .metallographic(List.of())
                                .build())
                        .build())
                .build();
    }

    /**
     * Расчёт массы связки
     *
     * @param requestMessage запрос типа CcmPtsRequest
     * @return расчётное значение
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
     * Подготовка общей спецификации
     *
     * @param requestMessage запрос типа CcmPtsRequest
     * @return список спецификации
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
     * Подготовка спецификации по Химии
     *
     * @param requestMessage запрос типа CcmPtsRequest
     * @return список спецификации по Химии
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
     * Подготовка свойств Механики для ЦТС
     *
     * @param requestMessage запрос типа CcmPtsRequest
     * @return список свойств Механики
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

}
