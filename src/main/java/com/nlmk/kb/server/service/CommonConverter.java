package com.nlmk.kb.server.service;

import com.nlmk.attestation.product.api.nsi.LimitDto;
import com.nlmk.kb.server.entity.pdm.Spec;

import javax.persistence.Tuple;
import java.util.Date;
import java.util.List;

public interface CommonConverter {

    public Date parseToDate(String stringDate);

    public String getSpecValue(List<Spec> specs, int code);

    public LimitDto stringToLimit(String value);

    public Double parseToDouble(String s);

    public Integer parseToInteger(String s);

    public List<Double> parseToDoubles(String s);

    String parseToStringByDatePattern(Date date, String pattern);

    String getByTupleAlias(Tuple t, String alias);
}
