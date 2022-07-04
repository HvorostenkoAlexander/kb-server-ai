package com.nlmk.kb.server.api.ccm.pts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum CcmPtsTypeCode {

    STRING(1, "Строка"),
    NUMBER(2, "Число"),
    DATE(3, "Дата");

    @JsonValue
    private final Integer value;
    private final String desc;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static CcmPtsTypeCode fromValue(int value) {
        return Arrays.stream(CcmPtsTypeCode.values())
                .filter(s -> s.getValue().equals(value))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unknown CcmPtsTypeCode value [%s]", value)));

    }

}
