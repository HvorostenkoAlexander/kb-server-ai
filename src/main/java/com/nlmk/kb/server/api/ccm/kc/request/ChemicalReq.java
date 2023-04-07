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
public class ChemicalReq {

    private final @NotNull Integer chemCode; // Код химического элемента
    private final @NotBlank String chemName; // Наименование химического элемента
    private final BigDecimal valueMin; // Минимальное значение химического элемента
    private final BigDecimal valueMax; // Максимальное значение химического элемента
    private final Integer digitsQuantity; //Количество знаков после запятой

}