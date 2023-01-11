package com.nlmk.kb.server.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpMethod;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum Operation {

    I(HttpMethod.POST),
    U(HttpMethod.PUT),
    D(HttpMethod.DELETE);

    private final HttpMethod httpMethod;

    public static Operation fromValue(String value) {
        return Arrays.stream(Operation.values())
                .filter(p -> p.name().equals(value))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Operation: неизвестное значение [%s]", value)));
    }

}
