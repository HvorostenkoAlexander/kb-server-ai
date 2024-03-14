package com.nlmk.kb.server.api.ccm.kc.request;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@Jacksonized
@SuppressWarnings("checkstyle:illegalidentifiername")
public class Record {

    private final @NotBlank String primeId; // id_slab Сквозной идентификатор сляба
    private final @NotNull Long werks; // Код завода
    private final @NotBlank String werksName; // Наименование завода
    private final @NotNull Integer workshop; // Код цеха
    private final @NotBlank String workshopName; // Наименование цеха
    private final Long orderNum; // Номер заказа
    private final Integer orderPos; // Позиция заказа
    private final @NotBlank String unitCode; // Код агрегата
    private final @NotBlank String unitName; // Наименование агрегата
    private final @NotNull BigDecimal weightNet; // Вес нетто (т.)
    private final @NotNull @Valid Marking marking; // Маркировка
    private final @NotNull @Valid Marking markingAcc; // Маркировка учетная
    private final @NotNull @Valid Requirements requirements; // Требования
    private final List<@Valid ChemData> chemData; // Химанализ
    private final List<@Valid Specification> specifications; // Основные характеристики единицы продукции

}
