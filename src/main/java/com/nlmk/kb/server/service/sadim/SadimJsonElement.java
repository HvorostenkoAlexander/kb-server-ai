package com.nlmk.kb.server.service.sadim;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum SadimJsonElement {

    TIME_ROLLING("time_rolling"),
    PRIME_ID("PRIME_ID"),
    T12_MIN("t12_min"),
    T12_MAX("t12_max"),
    TCM_MIN("tcm_min"),
    TCM_MAX("tcm_max"),
    PBI("PBI"),
    PROF_FACT("ProfFact"),
    WEDGE_FACT("WedgeFact"),
    SQC_CRIT_MAX("SQC_CRIT_MAX"),
    PH_1SGP("PH_1SGP"),
    PH_12SGP("PH_12SGP"),
    PH_23SGP("PH_23SGP"),
    ESTIMATE("estimate"),
    LCL_THCKNG("lclThckng"),
    LOT_NO("lot_no"),
    MELT_NO("melt_no"),
    //
    VALUES("values");

    private final String name;

    public static SadimJsonElement fromName(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        return Arrays.stream(SadimJsonElement.values())
                .filter(s -> s.getName().equals(name))
                .findAny()
                .orElse(null);
    }

}
