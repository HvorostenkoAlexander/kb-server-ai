package com.nlmk.kb.server.entity.pam;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MetallographicData {
    // 1.4.16.12 Данные, Металлографическая оценка стали

    private Integer metgrapCode; // Код характеристики
    private String metgrapName; // Наименование характеристики
    private String metgrapFormat; // Формат передачи характеристики
    private String metgrapValue; // Значение характеристики
    private String metgrapTypeCode; // Тип данных (1 - строка, 2 - число, 3 - дата)
    private String metgrapMeasure; // Единица измерения характеристики

}