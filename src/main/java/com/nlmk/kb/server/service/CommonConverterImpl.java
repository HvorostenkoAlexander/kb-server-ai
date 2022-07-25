package com.nlmk.kb.server.service;

import com.nlmk.attestation.product.api.nsi.LimitDto;
import com.nlmk.attestation.product.api.specification.SpecCode;
import com.nlmk.kb.server.entity.pdm.Spec;
import com.nlmk.kb.server.exception.DateTimeParseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class CommonConverterImpl implements CommonConverter {

    @Override
    public Date parseToDate(String stringDate) {

        Date dateMs = parseToDateWithMs(stringDate);
        if (dateMs != null) {
            return dateMs;
        }

        Date dateNoMs = parseToDateNoMs(stringDate);
        if (dateNoMs != null) {
            return dateNoMs;
        }

        Date dateNoTimeZone = parseToDateNoTimeZone(stringDate);
        if (dateNoTimeZone != null) {
            return dateNoTimeZone;
        }
        throw new DateTimeParseException("Ошибка парсинга ts: " + stringDate + "; ");
    }

    private Date parseToDateNoTimeZone(String stringDate) {
        return parse(stringDate, "yyyy-MM-dd'T'HH:mm:ss");
    }

    private Date parseToDateNoMs(String stringDate) {
        return parse(stringDate, "yyyy-MM-dd'T'HH:mm:ssX");
    }

    private Date parseToDateWithMs(String stringDate) {
        return parse(stringDate, "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    }

    private Date parse(String stringDate, String stringFormat) {
        final var format = new SimpleDateFormat(stringFormat);
        try {
            return format.parse(stringDate);
        } catch (ParseException e) {
            log.warn("ParseException: [{}] with stringDate:[{}], stringFormat:[{}]", e.getMessage(), stringDate, stringFormat);
            return null;
        }
    }

    @Override
    public String getSpecValue(List<Spec> specs, SpecCode specCode) {
        if (specs == null) {
            return null;
        }

        return specs.stream()
                .filter(Objects::nonNull)
                .filter(s -> s.getSpecCode() == specCode.getValue())
                .findFirst()
                .map(Spec::getSpecValue).orElse(null);
    }

    @Override
    public LimitDto stringToLimit(String value) {
        return LimitDto.builder()
                .srcValue(value)
                .build();
    }

    @Override
    public Double parseToDouble(String s) {
        Double d = null;
        if (!(StringUtils.isBlank(s) || "null".equals(s))) {
            try {
                d = Double.parseDouble(s);
            } catch (NumberFormatException nfe) {
                log.error("CommonConverterImpl::parsToDouble. Ошибка десериализации строки: {}, в Double.", s);
                throw nfe;
            }
        }
        return d;
    }

    @Override
    public Integer parseToInteger(String s) {
        Integer i = null;
        if (!(StringUtils.isBlank(s) || "null".equals(s))) {
            try {
                i = Integer.parseInt(s);
            } catch (NumberFormatException nfe) {
                log.error("CommonConverterImpl::parsToInteger. Ошибка десериализации строки: {}, в Integer.", s);
                throw nfe;
            }
        }
        return i;
    }

    @Override
    public String parseToStringByDatePattern(Date date, String pattern) {
        if (date == null || pattern == null) {
            return null;
        }
        try {
            DateFormat df = new SimpleDateFormat(pattern);
            String s = df.format(date);
            log.debug("toStringByPattern; date :" + s);
            return s;
        } catch (NullPointerException | IllegalArgumentException ex) {
            log.warn("Неверный шаблон для формата даты: [{}]", pattern);
            return null;
        }
    }

}
