package com.nlmk.kb.server.service;

import com.nlmk.attestation.product.api.nsi.LimitDto;
import com.nlmk.attestation.product.api.specification.SpecCode;
import com.nlmk.kb.server.entity.pdm.Spec;

import java.util.Date;
import java.util.List;

public interface CommonConverter {

    Date parseToDate(String stringDate);

    String getSpecValue(List<Spec> specs, SpecCode specCode);

    LimitDto stringToLimit(String value);

    Double parseToDouble(String s);

    Integer parseToInteger(String s);

    String parseToStringByDatePattern(Date date, String pattern);

}
