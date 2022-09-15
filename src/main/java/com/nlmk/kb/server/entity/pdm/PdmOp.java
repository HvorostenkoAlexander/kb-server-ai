package com.nlmk.kb.server.entity.pdm;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpMethod;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum PdmOp {

    I(HttpMethod.POST),
    U(HttpMethod.PUT),
    D(HttpMethod.DELETE);

    private final HttpMethod httpMethod;

    public static PdmOp fromValue(String value) {
        return Arrays.stream(PdmOp.values())
                .filter(p -> p.name().equals(value))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unknown PdmOp value [%s]", value)));
    }

}
