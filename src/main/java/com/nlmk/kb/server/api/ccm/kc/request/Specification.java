package com.nlmk.kb.server.api.ccm.kc.request;

import com.nlmk.kb.server.api.ccm.SpecTypeCode;
import com.nlmk.kb.server.api.ccm.SpecTypeValue;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@SuperBuilder
@Jacksonized
public class Specification {

    private final @NotNull Integer specCode; // Код характеристики
    private final @NotBlank String specName; // Наименование характеристики
    private final @NotNull SpecTypeCode specTypeCode; // Тип данных (1-строка, 2-число, 3-дата)
    private final @NotBlank String specTypeName; // Наименование типа данных
    private final @NotNull SpecTypeValue specTypeValue; // Тип значения (1 - простое, 2 - перечислимое)
    private final String specValue; // Значение
    private final List<@Valid SpecValue> listValues;
    private final String specDecryption; // Расшифровка справочного значения
    private final String specFormat; // Формат передачи характеристики
    private final String specMeasure; // Единица измерения

}
