package com.nlmk.kb.server.api.ccm.kc;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;

@Data
@SuperBuilder
@Jacksonized
public class Pk {

    private final @NotBlank String systemCode; // Код системы
    private final @NotBlank String id; // Идентификатор сляба

}
