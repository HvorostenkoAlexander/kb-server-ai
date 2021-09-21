package com.nlmk.kb.server.service.impl;

import com.nlmk.kb.server.entity.pam.AttestationRequest;
import com.nlmk.kb.server.entity.pam.ChemicalSpec;
import com.nlmk.kb.server.entity.pam.DataField;
import com.nlmk.kb.server.entity.pam.MechanicalData;
import com.nlmk.kb.server.entity.pam.MechanicalSpec;
import com.nlmk.kb.server.entity.pam.MetallographicData;
import com.nlmk.kb.server.entity.pam.MetallographicSpec;
import com.nlmk.kb.server.entity.pam.OrderRequest;
import com.nlmk.kb.server.entity.pam.Pk;
import com.nlmk.kb.server.entity.pam.Specs;
import com.nlmk.kb.server.entity.pam.Value;
import com.nlmk.kb.server.service.AttestationRequestConverter;
import com.nlmk.kb.server.service.CommonConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nlmk.l3.ccm.pgp.RecordChemical;
import nlmk.l3.ccm.pgp.RecordData;
import nlmk.l3.ccm.pgp.RecordMechData;
import nlmk.l3.ccm.pgp.RecordMechanical;
import nlmk.l3.ccm.pgp.RecordMetallographic;
import nlmk.l3.ccm.pgp.RecordMetgrapData;
import nlmk.l3.ccm.pgp.RecordOrderReq;
import nlmk.l3.ccm.pgp.RecordPk;
import nlmk.l3.ccm.pgp.RecordSpecifications;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttestatonRequestConverterImpl implements AttestationRequestConverter {

    private final CommonConverter converter;

    @Override
    public AttestationRequest toPamAttestationRequest(nlmk.l3.ccm.pgp.AttestationRequest ccmAttRequest) {
        Assert.notNull(ccmAttRequest, "ccmAttRequest is null");
        Assert.notNull(ccmAttRequest.getTs(), "ccmAttRequest.getTs() is null");
        Assert.notNull(ccmAttRequest.getOp(), "ccmAttRequest.getOp() is null");

        final var dateRequest = converter.parseToDate(ccmAttRequest.getTs().toString());
        Assert.notNull(dateRequest, "Не удалось получить сведения о ts в запроссе на аттестацию");

        final var value = Value.builder()
                .ts(dateRequest)
                .op(ccmAttRequest.getOp().toString());

        if (ccmAttRequest.getPk() != null) {
            value.pk(toPamPk(ccmAttRequest.getPk()));
        }
        if (ccmAttRequest.getData() != null) {
            value.data(toPamDataField(ccmAttRequest.getData()));
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
                .thickness(
                        toDouble(recordData.getThickness())
                )
                .width(
                        toDouble(recordData.getWidth())
                )
                .weightNet(
                        toDouble(recordData.getWeightNet())
                )
                .kceh(Long.valueOf(recordData.getKceh()));

        if (recordData.getOrderNum() != null) {
            dataFieldBuilder.orderNum(recordData.getOrderNum());
        }
        if (recordData.getOrderPos() != null) {
            dataFieldBuilder.orderPos(Long.valueOf(recordData.getOrderPos().intValue()));
        }
        if (recordData.getNplv() != null) {
            dataFieldBuilder.nplv(recordData.getNplv().longValue());
        }
        if (recordData.getHnum() != null) {
            dataFieldBuilder.hnum(recordData.getHnum().longValue());
        }
        if (recordData.getLength() != null) {
            dataFieldBuilder.length(
                    toDouble(recordData.getLength())
            );
        }
        if (recordData.getBundleWeight() != null) {
            dataFieldBuilder.bundleWeight(
                    toDouble(recordData.getBundleWeight())
            );
        }
        dataFieldBuilder.specifications(
                recordData.getSpecifications().stream()
                        .map(s -> toPamSpecs(s))
                        .collect(Collectors.toList())
        );
        if (recordData.getOrderReq() != null) {
            dataFieldBuilder.orderReq(
                    recordData.getOrderReq().stream()
                            .map(o -> toPamOrderRequest(o))
                            .collect(Collectors.toList())
            );
        }
        if (recordData.getChemical() != null) {
            dataFieldBuilder.chemical(
                    recordData.getChemical().stream()
                            .map(ch -> toPamChemicalSpec(ch))
                            .collect(Collectors.toList())
            );
        }
        if (recordData.getMechanical() != null) {
            dataFieldBuilder.mechanical(
                    recordData.getMechanical().stream()
                            .map(mech -> toPamMechanicalSpec(mech))
                            .collect(Collectors.toList())
            );
        }
        if (recordData.getMetallographic() != null) {
            dataFieldBuilder.metallographic(
                    recordData.getMetallographic().stream()
                            .map(mtl -> toPamMetallographicSpec(mtl))
                            .collect(Collectors.toList())
            );
        }
        return dataFieldBuilder.build();
    }

    private static Double toDouble(Float f) {
        return Double.parseDouble(Float.toString(f.floatValue()));
    }

    private static Specs toPamSpecs(RecordSpecifications specifications) {
        // установка значений полей, значения в которых не null согласно AVRO-схеме
        final var specs = Specs.builder()
                .specCode(specifications.getSpecCode())
                .specName(specifications.getSpecName().toString())
                .specTypeCode(specifications.getSpecTypeCode());

        if (specifications.getSpecValue() != null) {
            specs.specValue(specifications.getSpecValue().toString());
        }
        if (specifications.getSpecFormat() != null) {
            specs.specFormat(specifications.getSpecFormat().toString());
        }
        if (specifications.getSpecMeasure() != null) {
            specs.specMeasure(specifications.getSpecMeasure().toString());
        }
        return specs.build();
    }

    private static OrderRequest toPamOrderRequest(RecordOrderReq recordOrderReq) {
        final var orderRequest = OrderRequest.builder()
                .attrCode(recordOrderReq.getAttrCode())
                .attrName(recordOrderReq.getAttrName().toString())
                .attrTypeCode(recordOrderReq.getAttrTypeCode())
                .attrTypeValue(recordOrderReq.getAttrTypeValue());

        if (recordOrderReq.getAttrValue() != null) {
            orderRequest.attrValue(recordOrderReq.getAttrValue().toString());
        }

        if (recordOrderReq.getAttrFormat() != null) {
            orderRequest.attrFormat(recordOrderReq.getAttrFormat().toString());
        }

        if (recordOrderReq.getAttrMeasure() != null) {
            orderRequest.attrMeasure(recordOrderReq.getAttrMeasure().toString());
        }

        if (recordOrderReq.getListValues() != null) {
            orderRequest.listValues(
                    recordOrderReq.getListValues().stream()
                            .filter(r -> r != null)
                            .map(r -> r.getValue().toString())
                            .collect(Collectors.toList())
            );
        }
        return orderRequest.build();
    }

    private static ChemicalSpec toPamChemicalSpec(RecordChemical recordChemical) {
        final var chemicalSpec = ChemicalSpec.builder()
                .chemCode(recordChemical.getChemCode())
                .chemName(recordChemical.getChemName().toString());

        if (recordChemical.getChemValue() != null) {
            chemicalSpec.chemValue(recordChemical.getChemValue().toString());
        }
        if (recordChemical.getChemFormat() != null) {
            chemicalSpec.chemFormat(recordChemical.getChemFormat().toString());
        }
        return chemicalSpec.build();
    }

    private static MechanicalSpec toPamMechanicalSpec(RecordMechanical recordMechanical) {
        final var mechanicalSpec = MechanicalSpec.builder();

        if (recordMechanical.getTestArrayId() != null) {
            mechanicalSpec.testArrayId(recordMechanical.getTestArrayId().intValue());
        }
        if (recordMechanical.getHnum() != null) {
            mechanicalSpec.hnum(recordMechanical.getHnum().intValue());
        }
        if (recordMechanical.getProtId() != null) {
            mechanicalSpec.protId(recordMechanical.getProtId().intValue());
        }
        if (recordMechanical.getProtNum() != null) {
            mechanicalSpec.protNum(recordMechanical.getProtNum().intValue());
        }
        if (recordMechanical.getSampleId() != null) {
            mechanicalSpec.sampleId(recordMechanical.getSampleId().intValue());
        }
        if (recordMechanical.getProbeCode() != null) {
            mechanicalSpec.probeCode(recordMechanical.getProbeCode().intValue());
        }
        if (recordMechanical.getSampleNum() != null) {
            mechanicalSpec.sampleNum(recordMechanical.getSampleNum().intValue());
        }
        if (recordMechanical.getSignAnalysis() != null) {
            mechanicalSpec.signAnalysis(recordMechanical.getSignAnalysis().intValue());
        }
        if (recordMechanical.getFormationListNum() != null) {
            mechanicalSpec.formationListNum(recordMechanical.getFormationListNum().intValue());
        }
        if (recordMechanical.getProbeName() != null) {
            mechanicalSpec.probeName(recordMechanical.getProbeName().toString());
        }
        if (recordMechanical.getFormationListId() != null) {
            mechanicalSpec.formationListId(recordMechanical.getFormationListId().toString());
        }
        mechanicalSpec.mechData(
                recordMechanical.getMechData().stream()
                        .map(md -> toPamMechanicalData(md))
                        .collect(Collectors.toList())
        );
        return mechanicalSpec.build();
    }

    private static MechanicalData toPamMechanicalData(RecordMechData recordMechData) {
        final var mechanicalData = MechanicalData.builder()
                .mechCode(recordMechData.getMechCode())
                .mechName(recordMechData.getMechName().toString())
                .mechTypeCode(recordMechData.getMechTypeCode());

        if (recordMechData.getMechFormat() != null) {
            mechanicalData.mechFormat(recordMechData.getMechFormat().toString());
        }
        if (recordMechData.getMechValue() != null) {
            mechanicalData.mechValue(recordMechData.getMechValue().toString());
        }
        if (recordMechData.getMechMeasure() != null) {
            mechanicalData.mechMeasure(recordMechData.getMechMeasure().toString());
        }
        return mechanicalData.build();
    }

    private static MetallographicSpec toPamMetallographicSpec(RecordMetallographic recordMetallographic) {
        final var mtlSpec = MetallographicSpec.builder();

        if (recordMetallographic.getTestArrayId() != null) {
            mtlSpec.testArrayId(recordMetallographic.getTestArrayId().intValue());
        }
        if (recordMetallographic.getProtId() != null) {
            mtlSpec.protId(recordMetallographic.getProtId().intValue());
        }
        if (recordMetallographic.getProtNum() != null) {
            mtlSpec.protNum(recordMetallographic.getProtNum().intValue());
        }
        if (recordMetallographic.getSampleId() != null) {
            mtlSpec.sampleId(recordMetallographic.getSampleId().intValue());
        }
        if (recordMetallographic.getProbeName() != null) {
            mtlSpec.probeName(recordMetallographic.getProbeName().toString());
        }
        if (recordMetallographic.getProbeCode() != null) {
            mtlSpec.probeCode(recordMetallographic.getProbeCode().intValue());
        }
        if (recordMetallographic.getSampleNum() != null) {
            mtlSpec.sampleNum(recordMetallographic.getSampleNum().intValue());
        }
        if (recordMetallographic.getSignAnalysis() != null) {
            mtlSpec.signAnalysis(recordMetallographic.getSignAnalysis().intValue());
        }
        if (recordMetallographic.getFormationListId() != null) {
            mtlSpec.formationListId(recordMetallographic.getFormationListId().toString());
        }
        if (recordMetallographic.getFormationListNum() != null) {
            mtlSpec.formationListNum(recordMetallographic.getFormationListNum().intValue());
        }

        mtlSpec.metgrapData(
                recordMetallographic.getMetgrapData().stream()
                        .map(md -> toPamMetallographicData(md))
                        .collect(Collectors.toList())
        );

        return mtlSpec.build();
    }

    private static MetallographicData toPamMetallographicData(RecordMetgrapData recordMetgrapData) {
        final var mtlData = MetallographicData.builder()
                .metgrapCode(recordMetgrapData.getMetgrapCode())
                .metgrapName(recordMetgrapData.getMetgrapName().toString())
                .metgrapTypeCode(Integer.toString(recordMetgrapData.getMetgrapTypeCode()));

        if (recordMetgrapData.getMetgrapFormat() != null) {
            mtlData.metgrapFormat(recordMetgrapData.getMetgrapFormat().toString());
        }
        if (recordMetgrapData.getMetgrapValue() != null) {
            mtlData.metgrapValue(recordMetgrapData.getMetgrapValue().toString());
        }
        if (recordMetgrapData.getMetgrapMeasure() != null) {
            mtlData.metgrapMeasure(recordMetgrapData.getMetgrapMeasure().toString());
        }
        return mtlData.build();
    }
}