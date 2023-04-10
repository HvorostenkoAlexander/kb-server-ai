package com.nlmk.kb.server.api.ccm.kc.request;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;

@Data
@Builder
@Jacksonized
public class PlanTask {

    private final @NotNull Integer planTaskId; // Номер суточного задания
    private final @NotNull Integer planTaskLineId; // Идентификатор строки суточного задания

}