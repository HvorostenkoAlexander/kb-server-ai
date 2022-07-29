package com.nlmk.kb.server.service;

import com.nlmk.attestation.product.api.nsi.LimitDto;
import com.nlmk.attestation.product.api.specification.SpecCode;
import com.nlmk.kb.server.entity.pdm.Spec;

import java.util.Date;
import java.util.List;

public interface CommonConverter {

    Date parseToDate(String stringDate);

    /**
     * Получить строковое значение из Спецификации по коду Спецификации
     *
     * @param specs    список Спецификации сообщения
     * @param specCode код Спецификации
     * @return строковое значение или null
     */
    String getStringSpecValue(List<Spec> specs, SpecCode specCode);

    /**
     * Получить объект LimitDto по значению из Спецификации по коду Спецификации
     *
     * @param specs    список Спецификации сообщения
     * @param specCode код Спецификации
     * @return объект LimitDto
     */
    LimitDto getLimitSpecValue(List<Spec> specs, SpecCode specCode);

    Double parseToDouble(String s);

    Integer parseToInteger(String s);

    String parseToStringByDatePattern(Date date, String pattern);

}
