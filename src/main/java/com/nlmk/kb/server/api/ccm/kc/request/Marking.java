package com.nlmk.kb.server.api.ccm.kc.request;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;

@Data
@Builder
@Jacksonized
public class Marking {

    private final @NotNull Integer heat; // Номер плавки
    private final @NotNull Integer strand; // Номер машины
    private final @NotNull Integer slab; // Номер сляба

}