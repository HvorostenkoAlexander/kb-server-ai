package com.nlmk.kb.server.service.ccm.pts;

import com.nlmk.attestation.product.api.pam.AttestationRequest;
import com.nlmk.attestation.product.api.pam.DataField;
import com.nlmk.attestation.product.api.pam.Pk;
import com.nlmk.attestation.product.api.pam.Value;
import com.nlmk.kb.server.api.ccm.pts.CcmPtsRequest;
import com.nlmk.kb.server.service.CommonConverter;
import com.nlmk.kb.server.service.ccm.RestRequestAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CcmPtsRestRequestAdapterImpl implements RestRequestAdapter<CcmPtsRequest> {

    private final CommonConverter converter;

    @Override
    public AttestationRequest adapt(CcmPtsRequest requestMessage) {
        final var dateRequest = converter.parseToDate(requestMessage.getTs());
        final var data = requestMessage.getData();
        final var orderNum = data.getOrderNum() != null ? data.getOrderNum().longValue(): null;
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
                                //.bundleWeight( ?)
                                .kceh(data.getKceh().longValue())
                                .orderNum(orderNum)
                                .orderPos(orderPos)
                                // с версии 1.27.0 данные поля orderReq не используются, получение требований заказа через SAP
                                .orderReq(List.of())
                                // этих полей нет, заглушка todo
                                .specifications(List.of())
                                .chemical(List.of())
                                .mechanical(List.of())
                                .metallographic(List.of())
                                .build())
                        .build())
                .build();
    }

}
