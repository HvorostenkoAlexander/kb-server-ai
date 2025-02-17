package com.nlmk.kb.server.service.zifra;

import com.nlmk.kb.server.exception.ZifraMessageParserException;
import com.nlmk.kb.server.util.AdapterUtils;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public interface CatalogueParser<T> {

    String DATE_FORMAT = "yyyy-MM-dd";
    SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(DATE_FORMAT);
    String DATE_PARSER_EXCEPTION_MESSAGE = "Значение даты \"{0}\" не соответствует шаблону \"{1}\"";
    String INT_PARSER_EXCEPTION_MESSAGE = "Ошибка преобразования строки \"{0}\" в целое число";
    String NUMBER_PARSER_EXCEPTION_MESSAGE = "Ошибка преобразования строки \"{0}\" в число с плавающей точкой";
    String BOOL_PARSER_EXCEPTION_MESSAGE = "Ошибка преобразования логического значения \"{0}\" в boolean тип";

    /**
     * Тип обрабатываемого Каталога
     */
    Catalogue getCatalogue();

    /**
     * Получение готового Объекта заданного типа <code>T</code> для передачи в НСИ
     */
    T parse(nlmk.l3.nsi.zifra.pk pk, nlmk.l3.nsi.zifra.Data data);

    /**
     * Получение "Начало периода действия записи"
     */
    default Date getBeginDate(nlmk.l3.nsi.zifra.properties properties) {
        if (Objects.isNull(properties)) {
            return null;
        }

        final var value = AdapterUtils.sequenceToString(properties.getDateBegin());
        if (Objects.isNull(value)) {
            return null;
        }

        try {
            return DATE_FORMATTER.parse(value);
        } catch (ParseException e) {
            throw new ZifraMessageParserException(MessageFormat.format(DATE_PARSER_EXCEPTION_MESSAGE, value, DATE_FORMAT));
        }
    }

    /**
     * Получение "Окончание периода действия записи"
     */
    default Date getEndDate(nlmk.l3.nsi.zifra.properties properties) {
        if (Objects.isNull(properties)) {
            return null;
        }

        final var value = AdapterUtils.sequenceToString(properties.getDateEnd());
        if (Objects.isNull(value)) {
            return null;
        }

        try {
            return DATE_FORMATTER.parse(value);
        } catch (ParseException e) {
            throw new ZifraMessageParserException(MessageFormat.format(DATE_PARSER_EXCEPTION_MESSAGE, value, DATE_FORMAT));
        }
    }

    /**
     * Получение Атрибута "Признак активности записи"
     */
    default Boolean getActive(List<nlmk.l3.nsi.zifra.lineAttributes_record> lineAttributes) {
        if (CollectionUtils.isEmpty(lineAttributes)) {
            return false;
        }

        final var active = getAttrStringValueByName(lineAttributes, "active");
        if (Objects.isNull(active)) {
            return false;
        }

        return active.equals("true");
    }

    /**
     * Получение значения Атрибута в виде целого числа
     */
    default Integer getAttrIntegerValueByName(List<nlmk.l3.nsi.zifra.lineAttributes_record> lineAttributes, String name) {
        final var value = getAttrStringValueByName(lineAttributes, name);
        if (Objects.isNull(value)) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ZifraMessageParserException(MessageFormat.format(INT_PARSER_EXCEPTION_MESSAGE, value));
        }
    }

    /**
     * Получение значения Атрибута в виде числа с плавающей точкой
     */
    default BigDecimal getAttrBigDecimalValueByName(List<nlmk.l3.nsi.zifra.lineAttributes_record> lineAttributes, String name) {
        final var value = getAttrStringValueByName(lineAttributes, name);
        if (Objects.isNull(value)) {
            return null;
        }
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            throw new ZifraMessageParserException(MessageFormat.format(NUMBER_PARSER_EXCEPTION_MESSAGE, value));
        }
    }

    /**
     * Получение значения Атрибута в виде строки
     */
    default String getAttrStringValueByName(List<nlmk.l3.nsi.zifra.lineAttributes_record> lineAttributes, String name) {
        if (CollectionUtils.isEmpty(lineAttributes)) {
            return null;
        }

        return lineAttributes.stream()
                .filter(a -> Objects.nonNull(a.getAttrNameEng()))
                .filter(a -> AdapterUtils.sequenceToString(a.getAttrNameEng()).equals(name))
                .findFirst()
                .map(a -> Objects.isNull(a.getAttrValue()) ? null : a.getAttrValue().toString())
                .orElse(null);
    }

    /**
     * Получение значения Атрибута в виде boolean
     */
    default Boolean getAttrBooleanValueByName(List<nlmk.l3.nsi.zifra.lineAttributes_record> lineAttributes, String name) {
        final var value = getAttrStringValueByName(lineAttributes, name);
        if (Objects.isNull(value)) {
            return null;
        }
        try {
            return Boolean.valueOf(value);
        } catch (NumberFormatException e) {
            throw new ZifraMessageParserException(MessageFormat.format(BOOL_PARSER_EXCEPTION_MESSAGE, value));
        }
    }

}
