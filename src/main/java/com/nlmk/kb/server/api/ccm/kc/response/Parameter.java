package com.nlmk.kb.server.api.ccm.kc.response;

import com.nlmk.attestation.product.api.specification.TypeCode;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@Jacksonized
public class Parameter {

    @NotNull
    private final Integer code; // Код параметра
    @NotBlank
    private final String name; // Название параметра
    @NotBlank
    private final String value; // Значение параметра
    @NotNull
    private final TypeCode typeCode; // Тип параметра (1-строка, 2-число, 3-дата)
    @NotBlank
    private final String typeName; // Наименование типа параметра

}
