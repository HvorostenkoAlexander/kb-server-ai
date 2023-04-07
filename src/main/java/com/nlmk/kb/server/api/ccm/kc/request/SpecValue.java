package com.nlmk.kb.server.api.ccm.kc.request;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@Jacksonized
public class SpecValue {

    private final @NotBlank String value; // Значение
    private final String description; // Описание справочного значения

}
