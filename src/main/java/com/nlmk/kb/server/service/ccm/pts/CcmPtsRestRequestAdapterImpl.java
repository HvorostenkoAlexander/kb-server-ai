package com.nlmk.kb.server.service.ccm.pts;

import com.nlmk.attestation.product.api.pam.*;
import com.nlmk.kb.server.api.ccm.pts.CcmPtsRequest;
import com.nlmk.kb.server.service.CommonConverter;
import com.nlmk.kb.server.service.ccm.RestRequestAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CcmPtsRestRequestAdapterImpl extends CcmPtsRequestAdapter implements RestRequestAdapter<CcmPtsRequest> {

    private final CommonConverter converter;

    @Override
    public AttestationRequest adapt(CcmPtsRequest requestMessage) {
        final var dateRequest = converter.parseToDate(requestMessage.getTs());
        final var data = requestMessage.getData();

        return AttestationRequest.builder()
                .value(Value.builder()
                        .ts(dateRequest)
                        .op("I")
                        .pk(Pk.builder()
                                .systemCode(requestMessage.getPk().getSystemCode())
                                .id(requestMessage.getPk().getId())
                                .build())
                        .data(DataPts.builder()
                                .primeId(requestMessage.getPk().getId())
                                .nplv(data.getMarking().getNplv())
                                .hnum(data.getMarking().getHnum())
                                .roll(data.getMarking().getRoll().toString())
                                .length(data.getGeometry().getLength())
                                .thickness(data.getGeometry().getThickness())
                                .width(data.getGeometry().getWidth())
                                .weightNet(data.getWeightNet())
                                .bundleWeight(super.calcBundleWeight(requestMessage))
                                .kceh(data.getKceh())
                                .orderNum(data.getOrderNum())
                                .orderPos(data.getOrderPos())
                                .specifications(super.prepareSpecs(requestMessage))
                                .chemical(super.prepareChemicalSpecs(requestMessage))
                                .mechanicalPts(super.prepareMechanicalProperties(requestMessage))
                                .build())
                        .build())
                .build();
    }

}
