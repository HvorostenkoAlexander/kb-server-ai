package com.nlmk.kb.server.api.ccm.kc;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@Jacksonized
public class Pk {

    private final @NotBlank String systemCode; // Код системы
    private final @NotBlank String id; // Идентификатор сляба

}
