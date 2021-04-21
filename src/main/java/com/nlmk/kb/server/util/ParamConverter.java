package com.nlmk.kb.server.util;

import com.nlmk.kb.server.entity.IntegralParam;
import com.nlmk.kb.server.entity.RecData;
import com.nlmk.kb.server.entity.RecSpec;
import com.nlmk.kb.server.entity.UnrecoverableParam;
import nlmk.l3.sup.IntegralParameters;
import nlmk.l3.sup.RecordData;
import nlmk.l3.sup.RecordSpecifications;
import nlmk.l3.sup.UnrecoverableParametersTrends;
import org.springframework.util.Assert;

public class ParamConverter {
    private ParamConverter() {
        throw new RuntimeException("ParamConverter is utility class, only for create parameters entities.");
    }

    public static UnrecoverableParam toUnrecoverableParam(UnrecoverableParametersTrends up) {
        Assert.notNull(up, "Значение UnrecoverableParametersTrends не должно быть null");

        UnrecoverableParam unrecoverableParam = new UnrecoverableParam();
        unrecoverableParam.setOperation(up.getOp().toString());
        unrecoverableParam.setRecordPk(up.getPk().getId());
        unrecoverableParam.setTs(up.getTs().toString());

        if (up.getData() != null) {
            unrecoverableParam.setData(
                    toRecData(up.getData())
            );
        }

        return unrecoverableParam;
    }

    public static IntegralParam toIntegralParam(IntegralParameters ip) {
        Assert.notNull(ip, "Значение IntegralParameters не должно быть null");

        IntegralParam integralParam = new IntegralParam();
        integralParam.setOperation(ip.getOp().toString());
        integralParam.setRecordPk(ip.getPk().getId());
        integralParam.setTs(ip.getTs().toString());

        if (ip.getData() != null) {
            integralParam.setData(
                    toRecData(ip.getData())
            );
        }
        return integralParam;
    }

    private static RecData toRecData(RecordData recordData) {
        RecData recData = new RecData();
        recData.setKceh(recordData.getKceh());
        recData.setKcehName(recordData.getKcehName().toString());
        recData.setPrimeID(recordData.getPrimeId().toString());
        recData.setUnitCode(recordData.getUnitCode());
        recData.setUnitName(recordData.getUnitName().toString());
        recData.setWerksCode(recordData.getWerks());
        recData.setWerksName(recordData.getWerksName().toString());

        recordData.getSpecifications().forEach(
                rs -> recData.addSpec(
                        toRecSpec(rs)
                )
        );
        return recData;
    }

    private static RecSpec toRecSpec(RecordSpecifications recordSpecifications) {
        RecSpec recSpec = new RecSpec();
        recSpec.setSpecCode(recordSpecifications.getSpecCode());
        recSpec.setSpecName(recordSpecifications.getSpecName().toString());
        recSpec.setSpecTypeCode(recordSpecifications.getSpecTypeCode());
        recSpec.setSpecTypeName(recordSpecifications.getSpecTypeName().toString());

        if (recordSpecifications.getSpecFormat() != null) {
            recSpec.setSpecFormat(recordSpecifications.getSpecFormat().toString());
        }

        if (recordSpecifications.getSpecMeasure() != null) {
            recSpec.setSpecMeasure(recordSpecifications.getSpecMeasure().toString());
        }

        if (recordSpecifications.getSpecValue() != null) {
            recSpec.setSpecValue(recordSpecifications.getSpecValue());
        }
        return recSpec;
    }
}
