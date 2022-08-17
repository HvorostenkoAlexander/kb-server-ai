package com.nlmk.kb.server.service.ccm.pts;

import com.nlmk.attestation.product.api.pam.*;
import com.nlmk.attestation.product.api.pam.AttestationRequest;
import com.nlmk.kb.server.service.CommonConverter;
import com.nlmk.kb.server.service.ccm.KafkaRequestAdapter;
import lombok.RequiredArgsConstructor;
import nlmk.l3.ccm.pts.*;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CcmPtsKafkaRequestAdapterImpl implements KafkaRequestAdapter<nlmk.l3.ccm.pts.AttestationRequest> {

    private final CommonConverter converter;

    @Override
    public com.nlmk.attestation.product.api.pam.AttestationRequest adapt(nlmk.l3.ccm.pts.AttestationRequest requestMessagePts) {
        Assert.notNull(requestMessagePts, "requestMessagePts is null");
        Assert.notNull(requestMessagePts.getTs(), "requestMessagePts.getTs() is null");
        Assert.notNull(requestMessagePts.getOp(), "requestMessagePts.getOp() is null");

        final var dateRequest = converter.parseToDate(requestMessagePts.getTs().toString());
        Assert.notNull(dateRequest, "Не удалось получить сведения о ts в запросе на аттестацию");

        return AttestationRequest.builder()
                .value(Value.builder()
                        .ts(dateRequest)
                        .op(requestMessagePts.getOp().toString())
                        .pk(toPamPk(requestMessagePts.getPk()))
                        .data(toPamDataField(requestMessagePts.getData()))
                        .build())
                .build();
    }

    private static Pk toPamPk(RecordPk recordPk) {
        if (recordPk == null) {
            return null;
        }

        return Pk.builder()
                .systemCode(sequenceToString(recordPk.getSystemCode()))
                .id(sequenceToString(recordPk.getId()))
                .build();
    }

    private static DataField toPamDataField(RecordData recordData) {
        if (recordData == null) {
            return null;
        }

        // с версии 1.27.0 данные поля orderReq не используются, получение требований заказа через SAP
        // пустые списки дял полей specifications, chemical, mechanical, metallographic
        return DataField.builder()
                .primeId(recordData.getPrimeId().toString())
                .nplv(recordData.getNplv())
                .hnum(recordData.getHnum())
                .roll(recordData.getRoll().toString())
                .length(parseFloat(recordData.getLength()))
                .thickness(parseFloat(recordData.getThickness()))
                .width(parseFloat(recordData.getWidth()))
                .weightNet(parseFloat(recordData.getWeightNet()))
                .kceh(recordData.getKceh())
                .orderNum((long) recordData.getOrderNum())
                .orderPos(recordData.getOrderPos())
                .orderReq(List.of())
                .specifications(List.of())
                .chemical(List.of())
                .mechanical(List.of())
                .metallographic(List.of())
                .build();
    }

    private static Double parseFloat(Float f) {
        if (f == null) {
            return null;
        }
        return Double.parseDouble(Float.toString(f));
    }

    private static String sequenceToString(CharSequence sequence) {
        if (sequence == null) {
            return null;
        }
        return sequence.toString();
    }

}
