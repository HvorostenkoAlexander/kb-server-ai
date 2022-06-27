package com.nlmk.kb.server.service.ccm.pts;

import com.nlmk.kb.server.entity.pam.AttestationRequest;
import com.nlmk.kb.server.entity.pam.*;
import com.nlmk.kb.server.service.CommonConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nlmk.l3.ccm.pts.*;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttestationRequestPtsConverterImpl implements AttestationRequestPtsConverter {

    private final CommonConverter converter;

    @Override
    public AttestationRequest toPamAttestationRequest(nlmk.l3.ccm.pts.AttestationRequest ccmPtsRequest) {
        Assert.notNull(ccmPtsRequest, "ccmPtsRequest is null");
        Assert.notNull(ccmPtsRequest.getTs(), "ccmPtsRequest.getTs() is null");
        Assert.notNull(ccmPtsRequest.getOp(), "ccmPtsRequest.getOp() is null");

        final var dateRequest = converter.parseToDate(ccmPtsRequest.getTs().toString());
        Assert.notNull(dateRequest, "Не удалось получить сведения о ts в запросе на аттестацию");

        final var value = Value.builder()
                .ts(dateRequest)
                .op(ccmPtsRequest.getOp().toString());

        if (ccmPtsRequest.getPk() != null) {
            value.pk(toPamPk(ccmPtsRequest.getPk()));
        }
        if (ccmPtsRequest.getData() != null) {
            value.data(toPamDataField(ccmPtsRequest.getData()));
        }

        return AttestationRequest.builder()
                .value(value.build())
                .build();
    }

    private static Pk toPamPk(RecordPk recordPk) {
        Pk pk = new Pk();
        if (recordPk.getId() != null) {
            pk.setId(recordPk.getId().toString());
        }
        if (recordPk.getSystemCode() != null) {
            pk.setSystemCode(recordPk.getSystemCode().toString());
        }
        return pk;
    }

    private static DataField toPamDataField(RecordData recordData) {
        // установка значений полей, значения в которых не null согласно AVRO-схеме
        final var dataFieldBuilder = DataField.builder()
                .primeId(recordData.getPrimeId().toString())
                .roll(recordData.getRoll().toString())
                .thickness(toDouble(recordData.getThickness()))
                .width(toDouble(recordData.getWidth()))
                .weightNet(toDouble(recordData.getWeightNet()))
                .kceh((long) recordData.getKceh())
                .orderNum((long) recordData.getOrderNum())
                .orderPos((long) recordData.getOrderPos())
                // с версии 1.27.0 данные поля orderReq не используются, получение требований заказа через SAP
                .orderReq(List.of());

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
