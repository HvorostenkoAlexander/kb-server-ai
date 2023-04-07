package com.nlmk.kb.server.api.ccm.kc.request;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;

@Data
@SuperBuilder
@Jacksonized
public class PlanTask {

    private final @NotNull Integer planTaskId; // Номер суточного задания
    private final @NotNull Integer planTaskLineId; // Идентификатор строки суточного задания

}