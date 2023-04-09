package com.nlmk.kb.server.api.ccm.kc.response;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Data
@Builder
@Jacksonized
public class NormLimit {

    private final List<AccValue> listAccValues; // Перечень допустимых значений
    private final Double valueMin; // Минимальное допустимое значение
    private final Double valueMax; // Максимальное допустимое значение

}
