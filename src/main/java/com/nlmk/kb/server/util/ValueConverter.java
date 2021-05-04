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
import lombok.val;
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

    public static com.nlmk.kb.server.entity.pam.AttestationRequest fromKafkaAttestationRequest(AttestationRequest request) {

        val value = new Value();

        val attRequest = com.nlmk.kb.server.entity.pam.AttestationRequest.builder()
                .value(value)
                .build();

        //DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        LocalDateTime ldt = LocalDateTime.parse(request.getTs(), DateTimeFormatter.ISO_DATE_TIME);
        Date date = java.sql.Timestamp.valueOf(ldt);

        value.setTs(date);
        value.setOp(request.getOp().toString());

        if (request.getPk() != null) {
//            if (request.getPk().getId()!=null) {//todo уточнить по значению id
//                val reqId = Long.valueOf(request.getPk().getId().toString());
//                attRequest.setId(reqId);
//            }
            value.setPk(fromRequestPk(request.getPk()));
        }
        if (request.getData() != null) {
            value.setData(fromRequestRecordData(request.getData()));
        }

        return attRequest;
    }

    private static Pk fromRequestPk(RecordPk recordPk) {
        Pk pk = new Pk();
        if (recordPk.getId() != null) {
            pk.setId(recordPk.getId().toString());
        }
        if (recordPk.getSystemCode() != null) {
            pk.setSystemCode(recordPk.getSystemCode().toString());
        }
        return pk;
    }

    private static DataField fromRequestRecordData(RecordData recordData) {
        // установка значений полей, значения в которых не null согласно AVRO-схеме
        DataField dataField = DataField.builder()
                .primeId(recordData.getPrimeId().toString())
                .roll(recordData.getRoll().toString())
                .thickness(Double.valueOf(recordData.getThickness()))
                .width(Double.valueOf(recordData.getWidth()))
                .weightNet(Double.valueOf(recordData.getWeightNet()))
                .kceh(Long.valueOf(recordData.getKceh()))
                .orderNum(Long.valueOf(recordData.getOrderNum()))
                .orderPos(Long.valueOf(recordData.getOrderPos()))
                .build();
        if (recordData.getNplv() != null) {
            dataField.setNplv(recordData.getNplv().longValue());
        }
        if (recordData.getHnum() != null) {
            dataField.setHnum(recordData.getHnum().longValue());
        }
        if (recordData.getLength() != null) {
            dataField.setLength(recordData.getLength().doubleValue());
        }
        if (recordData.getBundleWeight() != null) {
            dataField.setBundleWeight(recordData.getBundleWeight().doubleValue());
        }
        dataField.setSpecifications(
                recordData.getSpecifications().stream()
                        .map(s -> fromRequestRecordSpecifications(s))
                        .collect(Collectors.toList())
        );
        dataField.setOrderReq(
                recordData.getOrderReq().stream()
                        .map(o -> fromRecordOrderRequest(o))
                        .collect(Collectors.toList())
        );
        dataField.setChemical(
                recordData.getChemical().stream()
                        .map(ch -> fromRecordChemical(ch))
                        .collect(Collectors.toList())
        );
        dataField.setMechanical(
                recordData.getMechanical().stream()
                        .map(mech -> fromRecordMechanical(mech))
                        .collect(Collectors.toList())
        );
        dataField.setMetallographic(
                recordData.getMetallographic().stream()
                        .map(mtl -> fromRecordMetallographic(mtl))
                        .collect(Collectors.toList())
        );
        return dataField;
    }

    private static Specs fromRequestRecordSpecifications(RecordSpecifications specifications) {
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

    private static OrderRequest fromRecordOrderRequest(RecordOrderReq recordOrderReq) {
        OrderRequest orderRequest = OrderRequest.builder()
                .attrCode(recordOrderReq.getAttrCode())
                .attrName(recordOrderReq.getAttrName().toString())
                .attrTypeCode(recordOrderReq.getAttrTypeCode())
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
        return orderRequest;
    }

    private static ChemicalSpec fromRecordChemical(RecordChemical recordChemical) {
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

    private static MechanicalSpec fromRecordMechanical(RecordMechanical recordMechanical) {
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
                        .map(md -> fromRecordMechData(md))
                        .collect(Collectors.toList())
        );
        return mechanicalSpec;
    }

    private static MechanicalData fromRecordMechData(RecordMechData recordMechData) {
        MechanicalData mechanicalData = MechanicalData.builder()
                .mechCode(recordMechData.getMechCode())
                .mechName(recordMechData.getMechName().toString())
                .mechTypeCode(recordMechData.getMechTypeCode())
                .build();

        if (recordMechData.getMechFormat() != null) {
            mechanicalData.setMechFormat(recordMechData.getMechFormat().toString());
        }
        if (recordMechData.getMechValue() != null) {
            mechanicalData.setMechFormat(recordMechData.getMechValue().toString());
        }
        if (recordMechData.getMechMeasure() != null) {
            mechanicalData.setMechMeasure(recordMechData.getMechMeasure().toString());
        }
        return mechanicalData;
    }

    private static MetallographicSpec fromRecordMetallographic(RecordMetallographic recordMetallographic) {
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
                        .map(md -> fromRecordMetgrapData(md))
                        .collect(Collectors.toList())
        );

        return mtlSpec;
    }

    private static MetallographicData fromRecordMetgrapData(RecordMetgrapData recordMetgrapData){
        MetallographicData mtlData = MetallographicData.builder()
                .metgrapCode(recordMetgrapData.getMetgrapCode())
                .metgrapName(recordMetgrapData.getMetgrapName().toString())
                .metgrapTypeCode(Integer.toString(recordMetgrapData.getMetgrapTypeCode()))
                .build();
        if (recordMetgrapData.getMetgrapFormat()!=null){
            mtlData.setMetgrapFormat(recordMetgrapData.getMetgrapFormat().toString());
        }
        if (recordMetgrapData.getMetgrapValue()!=null){
            mtlData.setMetgrapValue(recordMetgrapData.getMetgrapValue().toString());
        }
        if (recordMetgrapData.getMetgrapMeasure()!=null){
            mtlData.setMetgrapMeasure(recordMetgrapData.getMetgrapMeasure().toString());
        }
        return mtlData;
    }
}
