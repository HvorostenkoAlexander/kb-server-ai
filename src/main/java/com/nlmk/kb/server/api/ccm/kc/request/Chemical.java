package com.nlmk.kb.server.api.ccm.kc.request;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@SuperBuilder
@Jacksonized
public class Chemical {

    private final @NotNull Integer chemCode; // Код характеристики
    private final @NotBlank String chemName; // Наименование химического элемента
    private final @NotNull BigDecimal chemValue; // Значение химического элемента

}
