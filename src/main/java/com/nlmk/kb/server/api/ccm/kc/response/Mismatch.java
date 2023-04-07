package com.nlmk.kb.server.api.ccm.kc.response;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@SuperBuilder
@Jacksonized
public class Mismatch {

    @NotNull
    private final Integer code; // Код результата аттестации
    @NotBlank
    private final String name; // Название результата аттестации

}
