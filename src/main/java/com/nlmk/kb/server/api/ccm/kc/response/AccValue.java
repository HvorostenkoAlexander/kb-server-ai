package com.nlmk.kb.server.api.ccm.kc.response;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;

@Data
@SuperBuilder
@Jacksonized
public class AccValue {

    @NotBlank
    private final String value; // Допустимое значение

}
