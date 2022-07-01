package com.nlmk.kb.server.service.ccm.pts;

import com.nlmk.kb.server.service.CommonConverter;
import com.nlmk.kb.server.service.ccm.KafkaRequestAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nlmk.l3.ccm.pts.*;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CcmPtsKafkaRequestAdapterImpl implements KafkaRequestAdapter<AttestationRequest> {

    private final CommonConverter converter;

    @Override
    public com.nlmk.attestation.product.api.pam.AttestationRequest adapt(nlmk.l3.ccm.pts.AttestationRequest requestMessagePts) {
        Assert.notNull(requestMessagePts, "requestMessagePts is null");
        Assert.notNull(requestMessagePts.getTs(), "requestMessagePts.getTs() is null");
        Assert.notNull(requestMessagePts.getOp(), "requestMessagePts.getOp() is null");

        final var dateRequest = converter.parseToDate(requestMessagePts.getTs().toString());
        Assert.notNull(dateRequest, "Не удалось получить сведения о ts в запросе на аттестацию");

        final var value = com.nlmk.attestation.product.api.pam.Value.builder()
                .ts(dateRequest)
                .op(requestMessagePts.getOp().toString());

        if (requestMessagePts.getPk() != null) {
            value.pk(toPamPk(requestMessagePts.getPk()));
        }
        if (requestMessagePts.getData() != null) {
            value.data(toPamDataField(requestMessagePts.getData()));
        }

        return com.nlmk.attestation.product.api.pam.AttestationRequest.builder()
                .value(value.build())
                .build();
    }

    private static com.nlmk.attestation.product.api.pam.Pk toPamPk(RecordPk recordPk) {
        com.nlmk.attestation.product.api.pam.Pk pk = new com.nlmk.attestation.product.api.pam.Pk();
        if (recordPk.getId() != null) {
            pk.setId(recordPk.getId().toString());
        }
        if (recordPk.getSystemCode() != null) {
            pk.setSystemCode(recordPk.getSystemCode().toString());
        }
        return pk;
    }

    private static com.nlmk.attestation.product.api.pam.DataField toPamDataField(RecordData recordData) {
        // установка значений полей, значения в которых не null согласно AVRO-схеме
        final var dataFieldBuilder = com.nlmk.attestation.product.api.pam.DataField.builder()
                .primeId(recordData.getPrimeId().toString())
                .roll(recordData.getRoll().toString())
                .thickness(toDouble(recordData.getThickness()))
                .width(toDouble(recordData.getWidth()))
                .weightNet(toDouble(recordData.getWeightNet()))
                .kceh((long) recordData.getKceh())
                .orderNum((long) recordData.getOrderNum())
                .orderPos((long) recordData.getOrderPos())
                // с версии 1.27.0 данные поля orderReq не используются, получение требований заказа через SAP
                .orderReq(List.of())
                // этих полей нет, заглушка
                .specifications(List.of())
                .chemical(List.of())
                .mechanical(List.of())
                .metallographic(List.of());

        if (recordData.getNplv() != null) {
            dataFieldBuilder.nplv(recordData.getNplv().longValue());
        }
        if (recordData.getHnum() != null) {
            dataFieldBuilder.hnum(recordData.getHnum().longValue());
        }
        if (recordData.getLength() != null) {
            dataFieldBuilder.length(toDouble(recordData.getLength()));
        }
        // .. будут еще поля
        return dataFieldBuilder.build();
    }

    private static Double toDouble(Float f) {
        return Double.parseDouble(Float.toString(f));
    }

}
