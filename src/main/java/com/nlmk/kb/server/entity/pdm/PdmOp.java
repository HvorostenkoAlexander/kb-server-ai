package com.nlmk.kb.server.entity.pdm;

import java.util.Arrays;

public enum PdmOp {

    I, U, D;

    public static PdmOp fromValue(String value) {
        return Arrays.stream(PdmOp.values())
                .filter(p -> p.name().equals(value))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unknown PdmOp value [%s]", value)));
    }

}
