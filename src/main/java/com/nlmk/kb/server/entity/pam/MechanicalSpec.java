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
public class MechanicalSpec {

    // 1.4.15.1, Идентификатор испытательного массива
    private Integer testArrayId;
    // 1.4.15.2, Номер испытуемой горячекатаной партии
    private Integer hnum;
    // 1.4.15.3, ИД протокола
    private Integer protId;
    // 1.4.15.4, Номер протокола
    private Integer protNum;
    // 1.4.15.5, ИД пробы
    private Integer sampleId;
    // 1.4.15.6, Наименование вида пробы
    private String probeName;
    // 1.4.15.7, Код вида пробы
    private Integer probeCode;
    //1.4.15.8, Номер пробы (образца)
    private Integer sampleNum;

    /*
     * 1.4.15.9, Признак испытаний:
     * 1 - первичные;
     * 2 - повторные (2- образца);
     * 5 - стат. расчет;
     * 8 - контрольные «середина»;
     * 9 - контрольные «голова».
     *
     * Приоритет:
     * Повторные/Контрольные «середина»/контрольные «голова».
     * Первичное.
     * Стат. расчет.
     */
    private Integer signAnalysis;

    // 1.4.15.10, ИД формировочной карточки
    private String formationListId;
    // 1.4.15.11, Номер формировочной карточки
    private Integer formationListNum;
    // 1.4.15.12, Характеристики и результаты мех испытаний
    private List<MechanicalData> mechData;


}