package com.nlmk.kb.server.api.ccm.kc.response;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@Jacksonized
public class Attestation {

    @NotNull
    private final Integer groupCode; // Код группы характеристик
    @NotBlank
    private final String groupName; // Название группы характеристик
    @NotEmpty
    private final List<AttestationValue> listValues; // Результаты проверки характеристик из группыи

}
