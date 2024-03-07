package com.nlmk.kb.server.service.ccm.kc.kc1;

import com.nlmk.attestation.product.api.pam.AttestationRequest;
import com.nlmk.attestation.product.api.pam.DataKc;
import com.nlmk.attestation.product.api.pam.Pk;
import com.nlmk.attestation.product.api.pam.Value;
import com.nlmk.kb.server.api.ccm.kc.request.CcmKc1Request;
import com.nlmk.kb.server.service.CommonConverter;
import com.nlmk.kb.server.service.ccm.RestRequestAdapter;
import com.nlmk.kb.server.service.ccm.kc.CcmKcRestRequestAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CcmKc1RestRequestAdapterImpl extends CcmKcRestRequestAdapter implements RestRequestAdapter<CcmKc1Request> {

    private final CommonConverter converter;

    @Override
    public AttestationRequest adapt(CcmKc1Request requestMessage) {
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
                        .data(DataKc.builder()
                                .primeId(requestMessage.getPk().getId())
                                .werks(data.getWerks())
                                .werksName(data.getWerksName())
                                .kceh(data.getWorkshop())
                                .kcehName(data.getWorkshopName())
                                .orderNum(data.getOrderNum())
                                .orderPos(data.getOrderPos())
                                .unitCode(data.getUnitCode())
                                .unitName(data.getUnitName())
                                .weightNet(data.getWeightNet())
                                .marking(toMarking(data.getMarking()))
                                .markingAcc(toMarking(data.getMarkingAcc()))
                                .requirements(toRequirements(data.getRequirements()))
                                .chemData(toChemData(data.getChemData()))
                                .specifications(toSpecifications(data.getSpecifications()))
                                .build())
                        .build())
                .build();
    }

}
