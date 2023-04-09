package com.nlmk.kb.server.api.ccm.kc.response;

import com.nlmk.attestation.product.api.specification.TypeCode;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@Jacksonized
public class AttestationValue {

    @NotNull
    private final Integer code; // Код характеристики
    @NotBlank
    private final String name; // Наименование характеристики
    @NotNull
    private final TypeCode typeCode; // Тип данных (1-строка, 2-число, 3-дата)
    @NotBlank
    private final String typeName; // Наименование типа данных
    private final String value; // Значение характеристики
    private final String format; // Формат передачи характеристики
    private final String measure; // Единица измерения
    private final NormLimit normLimits; // С чем сверяли фактическое значение
    @NotNull
    private final Mismatch mismatch; // Результаты проверки параметра
    private final String note; // Пояснение результата проверки характеристики
    private final String defectSuggestion; // Текст рекомендации по устранению (не соответствия)
    private final List<Parameter> parameters; // Список параметров для аттестуемой характеристики

}
