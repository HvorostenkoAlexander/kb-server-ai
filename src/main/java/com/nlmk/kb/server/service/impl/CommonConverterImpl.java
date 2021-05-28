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
    public Date parseToDate(String stringDate) {
        val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        try {
            return format.parse(stringDate);
        } catch (ParseException e) {
            log.error("Ошибка парсинга ts: {}", stringDate);
            throw new RuntimeException("Ошибка парсинга ts: " + stringDate + "; " + e);
        }
    }

    @Override
    public String getSpecValue(List<Spec> specs, int code) {
        if (specs == null) {
            return null;
        }

        val spec = specs.stream().filter((s) -> s.getSpecCode() == code).findFirst();
        if (spec.isPresent()) {
            return spec.get().getSpecValue();
        } else {
            return null;
        }
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
        try {
            d = Double.parseDouble(s);
        } catch (NumberFormatException nfe) {
            log.error("CommonConverterImpl::parsToDouble. Ошибка десериализации строки: {}, в double.",s);
            throw nfe;
        } catch (NullPointerException e) {
            //log.debug("--- parsToDouble: " + e);
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
