package com.nlmk.kb.server.api.ccm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SpecTypeCode {
    STRING(1, "строка"),
    NUMBER(2, "число"),
    DATA(3, "дата");

    @JsonValue
    private final Integer value;
    private final String desc;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static SpecTypeCode fromValue(int value) {
        return Arrays.stream(SpecTypeCode.values())
                .filter(s -> s.getValue().equals(value))
                .findAny()
                .orElseThrow(
                        () -> new IllegalArgumentException(String.format("Unknown SpecTypeCode value [%s]", value)));
    }
}
