package com.nlmk.kb.server.api.ccm.kc.response;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Data
@SuperBuilder
@Jacksonized
public class NormLimit {

    private final List<AccValue> listAccValues; // Перечень допустимых значений
    private final Double valueMin; // Минимальное допустимое значение
    private final Double valueMax; // Максимальное допустимое значение

}
