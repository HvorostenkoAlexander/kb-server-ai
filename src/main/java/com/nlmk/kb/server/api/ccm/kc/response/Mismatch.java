package com.nlmk.kb.server.api.ccm.kc.response;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@Jacksonized
public class Mismatch {

    @NotNull
    private final Integer code; // Код результата аттестации
    @NotBlank
    private final String name; // Название результата аттестации

}
