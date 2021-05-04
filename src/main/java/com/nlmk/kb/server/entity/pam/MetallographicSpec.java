package com.nlmk.kb.server.entity.pam;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MetallographicSpec {
    // 1.4.16 Металлографическая оценка стали

    private Integer testArrayId; // Идентификатор испытательного массива
    private Long hnum; // Номер испытуемой горячекатаной партии
    private Integer protId; // ИД протокола
    private Integer protNum; // Номер протокола
    private Integer sampleId; // ИД пробы
    private String probeName; // Наименование вида пробы
    private Integer probeCode; // Код вида пробы
    private Integer sampleNum; // Номер пробы (образца)
    private Integer signAnalysis; // Признак испытаний
    private String formationListId; // ИД формировочной карточки
    private Integer formationListNum; // Номер формировочной карточки
    private List<MetallographicData> metgrapData; // Характеристики и результаты металлографии

}