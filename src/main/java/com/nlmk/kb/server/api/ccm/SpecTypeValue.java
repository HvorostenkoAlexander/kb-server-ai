package com.nlmk.kb.server.api.ccm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SpecTypeValue {

    SIMPLE(1, "простое"),
    ENUMERABLE(2, "перечислимое");

    @JsonValue
    private final Integer value;
    private final String desc;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static SpecTypeValue fromValue(int value) {
        return Arrays.stream(SpecTypeValue.values())
                .filter(s -> s.getValue().equals(value))
                .findAny()
                .orElseThrow(
                        () -> new IllegalArgumentException(String.format("Unknown SpecTypeValue value [%s]", value)));
    }
}
