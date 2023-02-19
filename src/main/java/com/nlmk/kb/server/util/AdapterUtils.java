package com.nlmk.kb.server.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.attestation.product.api.AttestationDto;
import com.nlmk.attestation.product.api.Status;
import com.nlmk.attestation.product.api.pam.DataField;
import com.nlmk.attestation.product.api.specification.SpecCode;
import com.nlmk.attestation.product.api.specification.TypeCode;

import java.math.BigDecimal;
import java.util.*;

/**
 * Вспомогательные методы для Адаптации типов
 */
public class AdapterUtils {

    private AdapterUtils() {
        throw new IllegalStateException("AdapterUtils is util class");
    }

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static BigDecimal toBigDecimal(Float f) {
        if (Objects.isNull(f) || Float.isNaN(f)) {
            return null;
        }
        return new BigDecimal(Float.toString(f));
    }

    public static BigDecimal toBigDecimal(Double d) {
        if (Objects.isNull(d) || Double.isNaN(d)) {
            return null;
        }
        return new BigDecimal(Double.toString(d));
    }

    public static String sequenceToString(CharSequence sequence) {
        if (Objects.isNull(sequence)) {
            return null;
        }
        return sequence.toString();
    }

    /**
     * Получение корректного типа данных для указанного кода спецификации
     */
    public static TypeCode getTypeCodeByCodeValue(Integer code) {
        if (Objects.isNull(code)) {
            return TypeCode.STRING;
        }
        try {
            return SpecCode.fromValue(code).getTypeCode();
        } catch (IllegalArgumentException e) {
            return TypeCode.STRING;
        }
    }

    /**
     * Определение значения Комментария
     */
    public static String detectNote(AttestationDto attestation) {
        if (Objects.isNull(attestation)) {
            return null;
        }

        if (Status.NOT_MATCHED_WITH_RECOMMENDATIONS == attestation.getStatus()
                || Status.MATCHED_MANUALLY == attestation.getStatus()) {
            return null;
        }
        return attestation.getComment();
    }

    /**
     * Определение значения Рекомендации по устранению дефекта
     */
    public static String detectDefectSuggestion(AttestationDto attestation) {
        if (Objects.isNull(attestation)) {
            return null;
        }

        if (Status.NOT_MATCHED_WITH_RECOMMENDATIONS == attestation.getStatus()
                || Status.MATCHED_MANUALLY == attestation.getStatus()) {
            return attestation.getComment();
        }
        return null;
    }

    /**
     * Подготовка списка Параметров одного результата Аттестации
     */
    public static Map<SpecCode, String> prepareParameters(AttestationDto attestation) {
        if (Objects.isNull(attestation) || Objects.isNull(attestation.getParams())) {
            return Map.of();
        }

        final var map = new EnumMap<SpecCode, String>(SpecCode.class);

        if (attestation.getParams().getKnctrator() != null) {
            map.put(SpecCode.CONCENTRATOR, attestation.getParams().getKnctrator());
        }
        if (attestation.getParams().getTemp() != null) {
            map.put(SpecCode.TEMPERATURE, attestation.getParams().getTemp());
        }
        if (attestation.getParams().getAnalysisId() != null) {
            map.put(SpecCode.ANALYSIS_ID, attestation.getParams().getAnalysisId().toString());
        }

        return map;
    }

    /**
     * Конвертация поля к типу DataField
     */
    public static Optional<DataField> getDataField(Object data) {
        try {
            return Optional.ofNullable(objectMapper.convertValue(data, DataField.class));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

}
