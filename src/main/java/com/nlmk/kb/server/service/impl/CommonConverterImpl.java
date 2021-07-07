package com.nlmk.kb.server.service.impl;

import com.nlmk.attestation.product.api.nsi.LimitDto;
import com.nlmk.kb.server.entity.pdm.Spec;
import com.nlmk.kb.server.exception.DateTimeParseException;
import com.nlmk.kb.server.service.CommonConverter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class CommonConverterImpl implements CommonConverter {

    @Override
    public Date parseToDate(String stringDate) {

        Date dateMs = parseToDateWithMs(stringDate);
        Date dateNoMs = parseToDateNoMs(stringDate);

        if (dateMs != null) {
            return dateMs;
        } else if (dateNoMs != null) {
            return dateNoMs;
        } else {
            throw new DateTimeParseException("Ошибка парсинга ts: " + stringDate + "; ");
        }
    }

    private Date parseToDateNoMs(String stringDate) {
        return parse(stringDate,"yyyy-MM-dd'T'HH:mm:ssX");
    }

    private Date parseToDateWithMs(String stringDate) {
        return parse(stringDate,"yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    }

    private Date parse(String stringDate, String stringFormat){
        val format = new SimpleDateFormat(stringFormat);
        try {
            return format.parse(stringDate);
        } catch (ParseException e) {
            return null;
        }
    }

    @Override
    public String getSpecValue(List<Spec> specs, int code) {
        if (specs == null) {
            return null;
        }

        val spec = specs.stream().filter((s) -> s.getSpecCode() == code).findFirst();
        return spec.map(Spec::getSpecValue).orElse(null);
    }

    @Override
    public LimitDto stringToLimit(String value) {
        return LimitDto.builder()
                .srcValue(value)
                .build();
    }

    @Override
    public Double parsToDouble(String s) {
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
    public Integer parsToInteger(String s) {
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
}
