package com.nlmk.kb.server.service.ccm.kc2;

import com.nlmk.attestation.product.api.pam.AttestationRequest;
import com.nlmk.attestation.product.api.pam.*;
import com.nlmk.kb.server.service.CommonConverter;
import com.nlmk.kb.server.service.ccm.KafkaRequestAdapter;
import com.nlmk.kb.server.util.AdapterUtils;
import lombok.RequiredArgsConstructor;
import nlmk.nlmk.l3.sus.kc2.DbAttestRequestVer;
import nlmk.nlmk.l3.sus.kc2.db.attestrequest.ver.*;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @link <a href="https://confluence.nlmk.com/pages/viewpage.action?pageId=103213250">Спецификация КЦ-2</a>
 */
@Component
@RequiredArgsConstructor
public class CcmKc2KafkaRequestAdapterImpl implements KafkaRequestAdapter<DbAttestRequestVer> {

    private final CommonConverter converter;

    @Override
    public AttestationRequest adapt(DbAttestRequestVer requestMessage) {
        Assert.notNull(requestMessage, "requestMessage is null");
        Assert.notNull(requestMessage.getTs(), "requestMessage.getTs() is null");
        Assert.notNull(requestMessage.getOp(), "requestMessage.getOp() is null");

        final var dateRequest = converter.parseToDate(AdapterUtils.sequenceToString(requestMessage.getTs()));
        Assert.notNull(dateRequest, "Не удалось получить сведения о ts в запросе на аттестацию");

        return AttestationRequest.builder()
                .value(Value.builder()
                        .ts(dateRequest)
                        .op(requestMessage.getOp().toString())
                        .pk(toPamPk(requestMessage.getPk()))
                        .data(toPamDataField(requestMessage.getPk(), requestMessage.getData()))
                        .build())
                .build();
    }

    private Pk toPamPk(nlmk.nlmk.l3.sus.kc2.db.attestrequest.ver.PkType recordPk) {
        if (Objects.isNull(recordPk)) {
            return null;
        }

        return Pk.builder()
                .systemCode(AdapterUtils.sequenceToString(recordPk.getSystemCode()))
                .id(AdapterUtils.sequenceToString(recordPk.getId()))
                .build();
    }

    private DataField toPamDataField(nlmk.nlmk.l3.sus.kc2.db.attestrequest.ver.PkType recordPk,
                                     nlmk.nlmk.l3.sus.kc2.db.attestrequest.ver.RecordData recordData) {
        if (Objects.isNull(recordData)) {
            return null;
        }

        return DataKc.builder()
                .kceh(recordData.getKceh())
                .primeId(Objects.nonNull(recordPk) ? AdapterUtils.sequenceToString(recordPk.getId()) : null)
                .heat(recordData.getMarking().getHeat())
                .strand(recordData.getMarking().getStrand())
                .slab(recordData.getMarking().getSlab())
                .orderNum(recordData.getOrderNum())
                .orderPos(recordData.getOrderPos())
                .requirements(toPamRequirement(recordData.getRequirements()))
                // + chemData
                .specifications(
                        recordData.getSpecifications().stream()
                                .map(this::toPamSpecs)
                                .collect(Collectors.toUnmodifiableList())
                )
                .build();
    }

    private Specs toPamSpecs(RecordDataSpecifications specifications) {
        return Specs.builder()
                .specCode(specifications.getSpecCode())
                .specName(AdapterUtils.sequenceToString(specifications.getSpecName()))
                .specTypeCode(specifications.getSpecTypeCode())
                .specValue(AdapterUtils.sequenceToString(specifications.getSpecValue()))
                .specFormat(AdapterUtils.sequenceToString(specifications.getSpecFormat()))
                .specMeasure(AdapterUtils.sequenceToString(specifications.getSpecMeasure()))
                .build();
    }

    private Requirement toPamRequirement(RecordRequirements recordRequirements) {
        return Requirement.builder()
                .chemicalReg(Objects.nonNull(recordRequirements.getChemicalReq()) ?
                        recordRequirements.getChemicalReq().stream()
                                .map(this::toPamChemicalReq)
                                .collect(Collectors.toUnmodifiableList()) :
                        null)
                .specifications(Objects.nonNull(recordRequirements.getSpecifications()) ?
                        recordRequirements.getSpecifications().stream()
                                .map(this::toPamSpecs)
                                .collect(Collectors.toUnmodifiableList()) :
                        null)
                .build();
    }

    private Specs toPamSpecs(RecordDataRequirementsSpecifications specifications) {
        return Specs.builder()
                .specCode(specifications.getSpecCode())
                .specName(AdapterUtils.sequenceToString(specifications.getSpecName()))
                .specTypeCode(specifications.getSpecTypeCode())
                .specValue(AdapterUtils.sequenceToString(specifications.getSpecValue()))
                .specFormat(AdapterUtils.sequenceToString(specifications.getSpecFormat()))
                .specMeasure(AdapterUtils.sequenceToString(specifications.getSpecMeasure()))
                .build();
    }

    private RequirementChemicalSpec toPamChemicalReq(RecordChemicalReq recordChemicalReg) {
        return RequirementChemicalSpec.builder()
                .chemCode(AdapterUtils.sequenceToString(recordChemicalReg.getChemCode()))
                .chemName(AdapterUtils.sequenceToString(recordChemicalReg.getChemName()))
                .valueMin(recordChemicalReg.getValueMin())
                .valueMax(recordChemicalReg.getValueMax())
                .build();
    }

}
