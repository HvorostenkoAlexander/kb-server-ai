package com.nlmk.kb.server.service.impl;

import com.nlmk.attestation.product.api.nsi.LimitDto;
import com.nlmk.kb.server.entity.pdm.Spec;
import com.nlmk.kb.server.service.CommonConverter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class CommonConverterImpl implements CommonConverter {

    @Override
    public Date parseToDate(String stringDate){
        val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        try {
            return format.parse(stringDate);
        } catch (ParseException e) {
            log.error("Ошибка парсинга ts: {}",stringDate);
            throw new RuntimeException("Ошибка парсинга ts: "+stringDate+"; "+e);
        }
    }

    @Override
    public String getSpecValue(List<Spec> specs, int code) {
        if (specs == null) {
            return null;
        }

        val spec = specs.stream().filter((s) -> s.getSpecCode() == code).findFirst();
        if (spec.isPresent()){
            return spec.get().getSpecValue();
        } else {
            return null;
        }
    }

    @Override
    public LimitDto stringToLimit(String value) {
        if (value == null || value.isEmpty() || value.isBlank()) {
            return null;
        }

        // чистка от мусора (только положительные числа) .. todo
        final var test = value.replaceAll("[^0-9,.*()\\[\\]]", "");
        // минимальный вариант - одиночное дробное значение, типа '1.0'
        if (test.length() < 3) {
            return null;
        }

        // Преобразование строкового представления в объект LimitDto по его правилам.
        //   Варианты значений:
        //           одиночные: '0.42'
        //            диапазон: '*..0.07' '0.030..0.050' '0.015..*'
        // уточненный диапазон: '(20..*' '[0.017..*' '[0.031..0.052)'
        // Для преобразования в Double нужен разделитель '.'
        // Разделитель '..' для диапазона чисел, в единственном числе.

        final var range = test.split("\\.\\.", 2);
        final var left = range[0].replaceAll(",", ".");
        final var leftDigit = left.replaceAll("[()\\[\\]]", "");

        // одиночное значение

        if (range.length == 1) {
            return LimitDto.builder()
                    .singleValue(Double.valueOf(leftDigit))
                    .build();
        }

        // диапазон

        final var right = range[1].replaceAll(",", ".");
        final var rightDigit = right.replaceAll("[()\\[\\]]", "");
        var builder = LimitDto.builder();

        if (left.contains("*")) {
            // диапазон открытый слева
            builder.closedRange(false).openedLeft(true)
                    .rightValue(Double.valueOf(rightDigit));
        } else if (right.contains("*")) {
            // диапазон открытый справа
            builder.closedRange(false).openedRight(true)
                    .leftValue(Double.valueOf(leftDigit));
        } else {
            builder.leftValue(Double.valueOf(leftDigit))
                    .rightValue(Double.valueOf(rightDigit));
        }
        // уточнение диапазона, по-умолчанию нестрогое неравенство, поиск строго
        if (left.contains("(")) {
            builder.strictLeft(true);
        }
        if (right.contains(")")) {
            builder.strictRight(true);
        }

        return builder.build();
    }

    @Override
    public Double parsToDouble(String s) {
        Double d = null;
        try {
            d = Double.parseDouble(s);
        } catch (NumberFormatException | NullPointerException e) {
            log.debug("--- parsToDouble: " + e);
        }
        return d;
    }

    @Override
    public Integer parsToInteger(String s) {
        Integer i = null;
        try {
            i = Integer.parseInt(s);
        } catch (NumberFormatException | NullPointerException e) {
            log.debug("--- parsToInteger: " + e);
        }
        return i;
    }
}
