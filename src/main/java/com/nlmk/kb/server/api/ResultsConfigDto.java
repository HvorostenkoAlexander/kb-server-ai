package com.nlmk.kb.server.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResultsConfigDto {

    private Long id;
    @NotBlank
    private String topic;
    @NotBlank
    private String avroName;
    private String condition;
    private boolean enabled;

}
