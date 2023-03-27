package com.nlmk.kb.server.api.ccm.kc;

import com.nlmk.kb.server.api.ccm.SpecTypeCode;
import com.nlmk.kb.server.api.ccm.SpecTypeValue;
import java.math.BigDecimal;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

/**
 * Запрос на Аттестацию Единицы Продукции, цех КЦ2<br>
 * Ссылка <a href="https://confluence.nlmk.com/pages/viewpage.action?pageId=166240974">Аттестация ЕП КЦ1,КЦ2 [2.1]</a>
 */
@Data
@Builder
@Jacksonized
public class CcmKc2Request {

    private final @NotBlank String ts; // Дата и время передачи
    private final @NotNull @Valid CcmKc2Request.Pk pk; // Первичный ключ
    private final @Valid CcmKc2Request.Record data; // Данные

    @Data
    @Builder
    @Jacksonized
    public static class Pk {
        private final @NotBlank String systemCode; // Код системы
        private final @NotBlank String id; // Идентификатор сляба
    }

    @Data
    @Builder
    @Jacksonized
    public static class Record {
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

    @Data
    @Builder
    @Jacksonized
    public static class Marking {
        private final @NotNull Integer heat; // Номер плавки
        private final @NotNull Integer strand; // Номер машины
        private final @NotNull Integer slab; // Номер сляба
    }

    @Data
    @Builder
    @Jacksonized
    public static class Requirements {
        private final @NotNull @Valid PlanTask planTask; // Плановое задание
        private final List<@Valid ChemicalReq> chemicalReq; // Список требований к хим. анализу
        private final List<@Valid Specification> specifications; // Список треб. характер. из суточного задания
    }

    @Data
    @Builder
    @Jacksonized
    public static class ChemicalReq {
        private final @NotNull Integer chemCode; // Код химического элемента
        private final @NotBlank String chemName; // Наименование химического элемента
        private final BigDecimal valueMin; // Минимальное значение химического элемента
        private final BigDecimal valueMax; // Максимальное значение химического элемента
        private final Integer digitsQuantity; //Количество знаков после запятой
    }

    @Data
    @Builder
    @Jacksonized
    public static class PlanTask {
        private final @NotNull Integer planTaskId; // Номер суточного задания
        private final @NotNull Integer planTaskLineId; // Идентификатор строки суточного задания
    }

    @Data
    @Builder
    @Jacksonized
    public static class ChemData {
        private final Long sampleId; // ИД пробы
        private final @NotBlank String probeCode; // Вид пробы ( C-сталь )
        private final @NotBlank String analysisCode; // Тип анализа (М-маркировочный. С-сляб, K-контрольный)
        private final Integer sampleNum; // Номер пробы (образца)
        private final @NotNull Integer heat; //  Номер плавки
        private final String samplingPlaceName; // Место отбора (Слябный К-концевая часть сляба, Н-головная часть сляба)
        private final String reason; // Причина отрезки пробы
        private final @NotEmpty List<@Valid Chemical> chemical; // Значения химического анализа
    }

    @Data
    @Builder
    @Jacksonized
    public static class Chemical {
        private final @NotNull Integer chemCode; // Код характеристики
        private final @NotBlank String chemName; // Наименование химического элемента
        private final @NotNull BigDecimal chemValue; // Значение химического элемента
    }

    @Data
    @Builder
    @Jacksonized
    public static class Specification {
        private final @NotNull Integer specCode; // Код характеристики
        private final @NotBlank String specName; // Наименование характеристики
        private final @NotNull SpecTypeCode specTypeCode; // Тип данных (1-строка, 2-число, 3-дата)
        private final @NotBlank String specTypeName; // Наименование типа данных
        private final @NotNull SpecTypeValue specTypeValue; // Тип значения (1 - простое, 2 - перечислимое)
        private final String specValue; // Значение
        private final List<@Valid SpecValue> listValues;
        private final String specDecryption; // Расшифровка справочного значения
        private final String specFormat; // Формат передачи характеристики
        private final String specMeasure; // Единица измерения
    }

    @Data
    @Builder
    @Jacksonized
    public static class SpecValue {
        private final @NotBlank String value; // Значение
        private final String description; // Описание справочного значения
    }

}
