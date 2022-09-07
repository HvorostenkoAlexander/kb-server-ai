package com.nlmk.kb.server.util;

import com.nlmk.attestation.product.api.specification.SpecCode;
import com.nlmk.attestation.product.api.specification.TypeCode;

public class AdapterUtils {

    private AdapterUtils() {
        throw new IllegalStateException("AdapterUtils is util class");
    }

    public static Double parseFloat(Float f) {
        if (f == null) {
            return null;
        }
        return Double.parseDouble(Float.toString(f));
    }

    public static String sequenceToString(CharSequence sequence) {
        if (sequence == null) {
            return null;
        }
        return sequence.toString();
    }

    /**
     * Получение корректного типа данных для указанного кода спецификации
     *
     * @param code значение кода Спецификации
     * @return объект TypeCode
     */
    public static TypeCode getTypeCodeByCodeValue(Integer code) {
        if (code == null) {
            return TypeCode.STRING;
        }
        try {
            return SpecCode.fromValue(code).getTypeCode();
        } catch (IllegalArgumentException e) {
            return TypeCode.STRING;
        }
    }

}
