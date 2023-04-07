package com.nlmk.kb.server.api.ccm.kc.request;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@SuperBuilder
@Jacksonized
public class Requirements {

    private final @NotNull @Valid PlanTask planTask; // Плановое задание
    private final List<@Valid ChemicalReq> chemicalReq; // Список требований к хим. анализу
    private final List<@Valid Specification> specifications; // Список треб. характер. из суточного задания

}
