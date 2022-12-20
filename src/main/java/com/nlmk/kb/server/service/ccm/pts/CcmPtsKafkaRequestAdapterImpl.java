package com.nlmk.kb.server.service.ccm.pts;

import com.nlmk.attestation.product.api.pam.*;
import com.nlmk.kb.server.config.AllowedCodesConfig;
import com.nlmk.kb.server.service.CommonConverter;
import com.nlmk.kb.server.service.ccm.KafkaRequestAdapter;
import com.nlmk.kb.server.util.AdapterUtils;
import nlmk.nlmk.l3.ccm.pts.db.attestation.request.ver1.PkType;
import nlmk.nlmk.l3.ccm.pts.db.attestation.request.ver1.RecordData;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class CcmPtsKafkaRequestAdapterImpl extends CcmPtsRequestAdapter implements KafkaRequestAdapter<nlmk.nlmk.l3.ccm.pts.DbAttestationRequestVer1> {

    private final CommonConverter converter;

    public CcmPtsKafkaRequestAdapterImpl(AllowedCodesConfig allowedCodesConfig,
                                         CommonConverter converter) {
        super(allowedCodesConfig);
        this.converter = converter;
    }

    @Override
    public AttestationRequest adapt(nlmk.nlmk.l3.ccm.pts.DbAttestationRequestVer1 requestMessagePts) {
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
                        .data(toPamDataField(requestMessagePts.getPk(), requestMessagePts.getData()))
                        .build())
                .build();
    }

    private Pk toPamPk(PkType recordPk) {
        if (recordPk == null) {
            return null;
        }

        return Pk.builder()
                .systemCode(AdapterUtils.sequenceToString(recordPk.getSystemCode()))
                .id(AdapterUtils.sequenceToString(recordPk.getId()))
                .build();
    }

    private DataField toPamDataField(PkType recordPk, RecordData recordData) {
        String primeId = null;
        if (recordPk != null) {
            primeId = AdapterUtils.sequenceToString(recordPk.getId());
        }
        if (recordData == null) {
            return null;
        }

        Integer hnum = null;
        Integer nplv = null;
        String roll = null;
        if (recordData.getMarking() != null) {
            hnum = recordData.getMarking().getHnum();
            nplv = recordData.getMarking().getNplv();
            roll = String.valueOf(recordData.getMarking().getRoll());
        }

        Double length = null;
        Double thickness = null;
        Double width = null;
        if (recordData.getGeometry() != null) {
            length = AdapterUtils.parseFloat(recordData.getGeometry().getLength());
            thickness = AdapterUtils.parseFloat(recordData.getGeometry().getThickness());
            width = AdapterUtils.parseFloat(recordData.getGeometry().getWidth());
        }

        return DataPts.builder()
                .primeId(primeId)
                .nplv(nplv)
                .hnum(hnum)
                .roll(roll)
                .length(length)
                .thickness(thickness)
                .width(width)
                .weightNet(AdapterUtils.parseFloat(recordData.getWeightNet()))
                .bundleWeight(super.calcBundleWeight(recordData))
                .kceh(recordData.getKceh())
                .orderNum(recordData.getOrderNum())
                .orderPos(recordData.getOrderPos())
                .specifications(super.prepareSpecs(recordData))
                .chemical(super.prepareChemicalSpecs(recordData))
                .mechanicalPts(super.prepareMechanicalProperties(recordData))
                .build();
    }

}
