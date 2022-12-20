package com.nlmk.kb.server.api;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@Jacksonized
public class ResultsConfigDto {

    private Integer id;
    private @NotBlank String topic;
    private @NotBlank String avroName;
    private String condition;
    private boolean enabled;

}
