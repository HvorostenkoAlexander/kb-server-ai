package com.nlmk.kb.server.util;

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
import nlmk.l3.ccm.pgp.AttestationRequest;
import nlmk.l3.ccm.pgp.RecordChemical;
import nlmk.l3.ccm.pgp.RecordData;
import nlmk.l3.ccm.pgp.RecordMechData;
import nlmk.l3.ccm.pgp.RecordMechanical;
import nlmk.l3.ccm.pgp.RecordMetallographic;
import nlmk.l3.ccm.pgp.RecordMetgrapData;
import nlmk.l3.ccm.pgp.RecordOrderReq;
import nlmk.l3.ccm.pgp.RecordPk;
import nlmk.l3.ccm.pgp.RecordSpecifications;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.stream.Collectors;

public class ValueConverter {
    private ValueConverter() {
        throw new RuntimeException("ValueConverter is utility class, only for create attestation request value.");
    }

    public static com.nlmk.kb.server.entity.pam.AttestationRequest toPamAttestationRequest(AttestationRequest request) {

        final var value = new Value();

        final var attRequest = com.nlmk.kb.server.entity.pam.AttestationRequest.builder()
                .value(value)
                .build();

        LocalDateTime ldt = LocalDateTime.parse(request.getTs(), DateTimeFormatter.ISO_DATE_TIME);
        Date date = java.sql.Timestamp.valueOf(ldt);

        value.setTs(date);
        value.setOp(request.getOp().toString());

        if (request.getPk() != null) {
            value.setPk(toPamPk(request.getPk()));
        }
        if (request.getData() != null) {
            value.setData(toPamDataField(request.getData()));
        }

        return attRequest;
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
            dataFieldBuilder.orderNum(Long.valueOf(recordData.getOrderNum().intValue()));
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
        Specs specs = Specs.builder()
                .specCode(specifications.getSpecCode())
                .specName(specifications.getSpecName().toString())
                .specTypeCode(specifications.getSpecTypeCode())
                .build();
        if (specifications.getSpecValue() != null) {
            specs.setSpecValue(specifications.getSpecValue().toString());
        }
        if (specifications.getSpecFormat() != null) {
            specs.setSpecFormat(specifications.getSpecFormat().toString());
        }
        if (specifications.getSpecMeasure() != null) {
            specs.setSpecMeasure(specifications.getSpecMeasure().toString());
        }
        return specs;
    }

    private static OrderRequest toPamOrderRequest(RecordOrderReq recordOrderReq) {
        OrderRequest orderRequest = OrderRequest.builder()
                .attrCode(recordOrderReq.getAttrCode())
                .attrName(recordOrderReq.getAttrName().toString())
                .attrTypeCode(recordOrderReq.getAttrTypeCode())
                .attrTypeValue(recordOrderReq.getAttrTypeValue())
                .build();

        if (recordOrderReq.getAttrValue() != null) {
            orderRequest.setAttrValue(recordOrderReq.getAttrValue().toString());
        }

        if (recordOrderReq.getAttrFormat() != null) {
            orderRequest.setAttrFormat(recordOrderReq.getAttrFormat().toString());
        }

        if (recordOrderReq.getAttrMeasure() != null) {
            orderRequest.setAttrMeasure(recordOrderReq.getAttrMeasure().toString());
        }

        if (recordOrderReq.getListValues() != null) {
            orderRequest.setListValues(
                    recordOrderReq.getListValues().stream()
                            .filter(r -> r != null)
                            .map(r -> r.getValue().toString())
                            .collect(Collectors.toList())
            );
        }
        return orderRequest;
    }

    private static ChemicalSpec toPamChemicalSpec(RecordChemical recordChemical) {
        ChemicalSpec chemicalSpec = ChemicalSpec.builder()
                .chemCode(recordChemical.getChemCode())
                .chemName(recordChemical.getChemName().toString())
                .build();
        if (recordChemical.getChemValue() != null) {
            chemicalSpec.setChemValue(recordChemical.getChemValue().toString());
        }
        if (recordChemical.getChemFormat() != null) {
            chemicalSpec.setChemFormat(recordChemical.getChemFormat().toString());
        }
        return chemicalSpec;
    }

    private static MechanicalSpec toPamMechanicalSpec(RecordMechanical recordMechanical) {
        MechanicalSpec mechanicalSpec = MechanicalSpec.builder()
                .build();
        if (recordMechanical.getTestArrayId() != null) {
            mechanicalSpec.setTestArrayId(recordMechanical.getTestArrayId().intValue());
        }
        if (recordMechanical.getHnum() != null) {
            mechanicalSpec.setHnum(recordMechanical.getHnum().intValue());
        }
        if (recordMechanical.getProtId() != null) {
            mechanicalSpec.setProtId(recordMechanical.getProtId().intValue());
        }
        if (recordMechanical.getProtNum() != null) {
            mechanicalSpec.setProtNum(recordMechanical.getProtNum().intValue());
        }
        if (recordMechanical.getSampleId() != null) {
            mechanicalSpec.setSampleId(recordMechanical.getSampleId().intValue());
        }
        if (recordMechanical.getProbeCode() != null) {
            mechanicalSpec.setProbeCode(recordMechanical.getProbeCode().intValue());
        }
        if (recordMechanical.getSampleNum() != null) {
            mechanicalSpec.setSampleNum(recordMechanical.getSampleNum().intValue());
        }
        if (recordMechanical.getSignAnalysis() != null) {
            mechanicalSpec.setSignAnalysis(recordMechanical.getSignAnalysis().intValue());
        }
        if (recordMechanical.getFormationListNum() != null) {
            mechanicalSpec.setFormationListNum(recordMechanical.getFormationListNum().intValue());
        }
        if (recordMechanical.getProbeName() != null) {
            mechanicalSpec.setProbeName(recordMechanical.getProbeName().toString());
        }
        if (recordMechanical.getFormationListId() != null) {
            mechanicalSpec.setFormationListId(recordMechanical.getFormationListId().toString());
        }
        mechanicalSpec.setMechData(
                recordMechanical.getMechData().stream()
                        .map(md -> toPamMechanicalData(md))
                        .collect(Collectors.toList())
        );
        return mechanicalSpec;
    }

    private static MechanicalData toPamMechanicalData(RecordMechData recordMechData) {
        MechanicalData mechanicalData = MechanicalData.builder()
                .mechCode(recordMechData.getMechCode())
                .mechName(recordMechData.getMechName().toString())
                .mechTypeCode(recordMechData.getMechTypeCode())
                .build();

        if (recordMechData.getMechFormat() != null) {
            mechanicalData.setMechFormat(recordMechData.getMechFormat().toString());
        }
        if (recordMechData.getMechValue() != null) {
            mechanicalData.setMechValue(recordMechData.getMechValue().toString());
        }
        if (recordMechData.getMechMeasure() != null) {
            mechanicalData.setMechMeasure(recordMechData.getMechMeasure().toString());
        }
        return mechanicalData;
    }

    private static MetallographicSpec toPamMetallographicSpec(RecordMetallographic recordMetallographic) {
        MetallographicSpec mtlSpec = MetallographicSpec.builder().build();

        if (recordMetallographic.getTestArrayId() != null) {
            mtlSpec.setTestArrayId(recordMetallographic.getTestArrayId().intValue());
        }
        if (recordMetallographic.getProtId() != null) {
            mtlSpec.setProtId(recordMetallographic.getProtId().intValue());
        }
        if (recordMetallographic.getProtNum() != null) {
            mtlSpec.setProtNum(recordMetallographic.getProtNum().intValue());
        }
        if (recordMetallographic.getSampleId() != null) {
            mtlSpec.setSampleId(recordMetallographic.getSampleId().intValue());
        }
        if (recordMetallographic.getProbeName() != null) {
            mtlSpec.setProbeName(recordMetallographic.getProbeName().toString());
        }
        if (recordMetallographic.getProbeCode() != null) {
            mtlSpec.setProbeCode(recordMetallographic.getProbeCode().intValue());
        }
        if (recordMetallographic.getSampleNum() != null) {
            mtlSpec.setSampleNum(recordMetallographic.getSampleNum().intValue());
        }
        if (recordMetallographic.getSignAnalysis() != null) {
            mtlSpec.setSignAnalysis(recordMetallographic.getSignAnalysis().intValue());
        }
        if (recordMetallographic.getFormationListId() != null) {
            mtlSpec.setFormationListId(recordMetallographic.getFormationListId().toString());
        }
        if (recordMetallographic.getFormationListNum() != null) {
            mtlSpec.setFormationListNum(recordMetallographic.getFormationListNum().intValue());
        }

        mtlSpec.setMetgrapData(
                recordMetallographic.getMetgrapData().stream()
                        .map(md -> toPamMetallographicData(md))
                        .collect(Collectors.toList())
        );

        return mtlSpec;
    }

    private static MetallographicData toPamMetallographicData(RecordMetgrapData recordMetgrapData) {
        MetallographicData mtlData = MetallographicData.builder()
                .metgrapCode(recordMetgrapData.getMetgrapCode())
                .metgrapName(recordMetgrapData.getMetgrapName().toString())
                .metgrapTypeCode(Integer.toString(recordMetgrapData.getMetgrapTypeCode()))
                .build();
        if (recordMetgrapData.getMetgrapFormat() != null) {
            mtlData.setMetgrapFormat(recordMetgrapData.getMetgrapFormat().toString());
        }
        if (recordMetgrapData.getMetgrapValue() != null) {
            mtlData.setMetgrapValue(recordMetgrapData.getMetgrapValue().toString());
        }
        if (recordMetgrapData.getMetgrapMeasure() != null) {
            mtlData.setMetgrapMeasure(recordMetgrapData.getMetgrapMeasure().toString());
        }
        return mtlData;
    }
}
