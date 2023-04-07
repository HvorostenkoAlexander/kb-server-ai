package com.nlmk.kb.server.api.ccm.kc.request;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;

@Data
@SuperBuilder
@Jacksonized
public class Marking {

    private final @NotNull Integer heat; // Номер плавки
    private final @NotNull Integer strand; // Номер машины
    private final @NotNull Integer slab; // Номер сляба

}