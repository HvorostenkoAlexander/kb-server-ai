package com.nlmk.kb.server.api.ccm.pts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.nlmk.attestation.product.api.pam.AnalysisValue;
import com.nlmk.attestation.product.api.specification.TypeCode;
import com.nlmk.kb.server.api.ccm.SpecTypeValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * Запрос на Аттестацию Единицы Продукции, цех ЦТС<br>
 * Ссылка <a href="https://confluence.nlmk.com/pages/viewpage.action?pageId=120034208">Аттестация ЕП ЦТС</a>
 */
@Data
@Builder
@Jacksonized
public class CcmPtsRequest {

    private final @NotBlank String ts; // Дата и время передачи
    private final @NotNull @Valid Pk pk; // Первичный ключ
    private final @Valid Record data; // Данные

    @Data
    @Builder
    @Jacksonized
    public static class Pk {
        private final @NotBlank String systemCode; // Код системы
        private final @NotBlank String id; // ИД единицы металла (ЕМ)
    }

    @Data
    @Builder
    @Jacksonized
    public static class Record {
        private final @NotNull Integer werks; // Код завода
        private final @NotBlank String werksName; // Наименование завода
        private final @NotNull Integer kceh; // Код цеха
        private final @NotBlank String kcehName; // Наименование цеха
        private final @NotNull Integer unitCode; // Код агрегата
        private final @NotBlank String unitName; // Наименование агрегата
        private final @NotNull Integer storageCode; // Код склада
        private final @NotBlank String storageName; // Наименование склада
        private final @NotNull @Valid Marking marking; // Маркировка ЕМ
        private final @NotNull BigDecimal weightNet; // Вес нетто (т.)
        private final @NotNull @Valid Geometry geometry; // Геометрия
        private final Long orderNum; // Номер заказа
        private final Integer orderPos; // Позиция заказа
        private final Integer unionId; // Идентификатор упаковки бунтов
        private final List<@Valid Bundle> bundles; // Список бунтов входящих в одну связку
        private final @NotEmpty List<@Valid Specification> specifications; // Список дополнительных характеристик
        private final List<@Valid OneProperty> properties; // Результаты магнитных свойств
        private final List<@Valid Chemical> chemical; // Химия
    }

    @Data
    @Builder
    @Jacksonized
    public static class Marking {
        private final @NotNull Integer nplv; // Номер плавки
        private final @NotNull Integer hnum; // Номер ГК партии
        private final @NotNull Integer tnum; // Номер ХК партии
        private final @NotNull Integer roll; // Номер рулона
        private final Integer strip; // Номер бунта
    }

    @Data
    @Builder
    @Jacksonized
    public static class Geometry {
        private final @NotNull BigDecimal thickness; // Толщина, мм
        private final @NotNull BigDecimal width; // Ширина, мм
        private final BigDecimal length; // Длина, мм
    }

    @Data
    @Builder
    @Jacksonized
    public static class Bundle {
        private final @NotNull Long stripId; // Идентификатор бунта
        private final @NotNull Integer stripNum; // Номер бунта
        private final @NotNull BigDecimal stripWidth; // Ширина бунта
        private final @NotNull BigDecimal stripWeight; // Вес бунта (т.)
    }

    @Data
    @Builder
    @Jacksonized
    public static class Specification {
        private final @NotNull Integer specCode; // Код характеристики
        private final @NotBlank String specName; // Наименование характеристики
        private final @NotNull Integer specTypeCode; // Тип данных
        private final @NotBlank String specTypeName; // Наименование типа данных
        private final @NotNull SpecTypeValue specTypeValue; // Тип значения (1 - простое, 2 - перечислимое)
        private final String specValue; // Значение
        private final List<@Valid OneSpecValue> listValues;
        private final String specDecryption; // Расшифровка справочного значения
        private final String specFormat; // Формат передачи характеристики
        private final String specMeasure; // Единица измерения
    }

    @Data
    @Builder
    @Jacksonized
    public static class OneSpecValue {
        private final @NotBlank String value; // Значение
        private final String description; // Описание справочного значения
    }

    @Data
    @Builder
    @Jacksonized
    public static class OneProperty {
        private final @NotNull Integer probeCode; // Код вида пробы
        private final @NotNull String probeName; // Наименование вида пробы
        private final @NotNull String testDate; // Дата и время испытания YYYY-MM-DD"T"HH24:MI:SS+/-HH:MM
        private final @NotNull Integer typeCode; // Код типа испытания
        private final @NotBlank String typeName; // Наименование типа испытания
        private final @NotEmpty List<@Valid OnePropAnalyze> analyzes; // Анализы
        private final List<@Valid OnePropValue> listValues; // Дополнительные результаты
        private final List<@Valid OnePropAtt> attestationList; // Список аттестаций
    }

    @Data
    @Builder
    @Jacksonized
    public static class OnePropValue {
        private final @NotNull Integer attrCode; // Код атрибута
        private final @NotNull TypeCode attrType; // Тип атрибута (1 - Строка, 2 - Число, 3 - Дата)
        private final @Valid List<OnePropValueAttr> attrValue; // Значения атрибута
        private final String attrFormat; // Формат атрибута
        private final String attrMeasure; // Единица измерения атрибута
    }

    @Data
    @Builder
    @Jacksonized
    public static class OnePropValueAttr {
        private final @NotBlank String value;
    }

    @Data
    @Builder
    @Jacksonized
    public static class OnePropAnalyze {
        private final @NotNull Integer samplingPlaceCode; // Код места отбора пробы
        private final @NotBlank String samplingPlaceName; // Наименование места отбора пробы
        private final @NotNull AnalysisValue analysisValue; // Результат (1-Худший, 2-Лучший)
        private final @NotEmpty List<@Valid OneAnalyzeValue> listValues; // Список значений
    }

    @Data
    @Builder
    @Jacksonized
    public static class OneAnalyzeValue {
        private final @NotNull Integer attrCode; // Код атрибута
        private final @NotNull TypeCode attrType; // Тип атрибута (1 - Строка, 2 - Число, 3 - Дата)
        private final String attrValue; // Значение атрибута
        private final String attrFormat; // Формат атрибута
        private final String attrMeasure; // Единица измерения атрибута
    }

    @Data
    @Builder
    @Jacksonized
    public static class OnePropAtt {
        private final @NotNull Integer typeCode; // Код аттестации*
        private final @NotBlank String typeName; // Наименование аттестации
        private final List<@Valid OneAttValue> listValues; // Список значений
    }

    @Data
    @Builder
    @Jacksonized
    public static class OneAttValue {
        private final @NotNull Side side; // Сторона (1 - Лицевая, 2 - Обратная, 3 - Обе стороны)
        private final @NotNull Integer attrCode; // Код атрибута
        private final @NotNull BigDecimal attrValue; // Значение атрибута
    }

    @Getter
    @AllArgsConstructor
    public enum Side {

        FRONT(1, "Лицевая"),
        BACK(2, "Обратная"),
        BOTH_SIDES(3, "Обе стороны");

        @JsonValue
        private final Integer value;
        private final String desc;

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public static Side fromValue(int value) {
            return Arrays.stream(Side.values())
                    .filter(s -> s.getValue().equals(value))
                    .findAny()
                    .orElseThrow(() -> new IllegalArgumentException(String.format("Unknown Side value [%s]", value)));
        }
    }

    @Data
    @Builder
    @Jacksonized
    public static class Chemical {
        private final @NotNull Integer id; // Идентификатор хим анализа
        private final List<@Valid OneChemicalValue> listValues; // Список хим.элементов
    }

    @Data
    @Builder
    @Jacksonized
    public static class OneChemicalValue {
        private final @NotNull Integer code; // Код химического элемента
        private final @NotBlank String name; // Наименование химического элемента
        private final BigDecimal value; // Значение химического элемента
    }

}
