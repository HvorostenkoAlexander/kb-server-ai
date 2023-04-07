package com.nlmk.kb.server.api.ccm.kc.request;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;

@Data
@SuperBuilder
@Jacksonized
public class SpecValue {

    private final @NotBlank String value; // Значение
    private final String description; // Описание справочного значения

}
