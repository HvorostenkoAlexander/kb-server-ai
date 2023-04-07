package com.nlmk.kb.server.api.ccm.kc.response;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@Jacksonized
public class AccValue {

    @NotBlank
    private final String value; // Допустимое значение

}
