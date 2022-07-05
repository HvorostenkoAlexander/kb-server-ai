package com.nlmk.kb.server.service.ccm.pts;

import com.nlmk.attestation.product.api.pam.*;
import com.nlmk.kb.server.api.ccm.pts.CcmPtsRequest;
import com.nlmk.kb.server.service.CommonConverter;
import com.nlmk.kb.server.service.ccm.RestRequestAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CcmPtsRestRequestAdapterImpl implements RestRequestAdapter<CcmPtsRequest> {

    private final CommonConverter converter;

    @Override
    public AttestationRequest adapt(CcmPtsRequest requestMessage) {
        final var dateRequest = converter.parseToDate(requestMessage.getTs());
        final var data = requestMessage.getData();
        final var orderNum = data.getOrderNum() != null ? data.getOrderNum().longValue() : null;
        final var orderPos = data.getOrderPos() != null ? data.getOrderPos().longValue() : null;

        return AttestationRequest.builder()
                .value(Value.builder()
                        .ts(dateRequest)
                        .op("I") // ?
                        .pk(new Pk(requestMessage.getPk().getId(), requestMessage.getPk().getSystemCode()))
                        .data(DataField.builder()
                                .primeId(requestMessage.getPk().getId())
                                .nplv(data.getMarking().getNplv().longValue())
                                .hnum(data.getMarking().getHnum().longValue())
                                .roll(data.getMarking().getRoll().toString())
                                .length(data.getGeometry().getLength())
                                .thickness(data.getGeometry().getThickness())
                                .width(data.getGeometry().getWidth())
                                .weightNet(data.getWeightNet())
                                //.bundleWeight() // ?
                                .kceh(data.getKceh().longValue())
                                .orderNum(orderNum)
                                .orderPos(orderPos)
                                // с версии 1.27.0 данные поля orderReq не используются, получение требований заказа через SAP
                                .orderReq(List.of())
                                .specifications(prepareSpecs(requestMessage))
                                .chemical(prepareChemicalSpecs(requestMessage))
                                // этих полей нет, заглушка todo
                                .mechanical(List.of())
                                .metallographic(List.of())
                                .build())
                        .build())
                .build();
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

}
