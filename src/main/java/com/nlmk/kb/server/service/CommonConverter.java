package com.nlmk.kb.server.service;

import com.nlmk.attestation.product.api.nsi.LimitDto;
import com.nlmk.kb.server.entity.pdm.Spec;

import java.util.Date;
import java.util.List;

public interface CommonConverter {

    public Date parseToDate(String stringDate);

    public String getSpecValue(List<Spec> specs, int code);

    public LimitDto stringToLimit(String value);

    public Double parsToDouble(String s);

    public Integer parsToInteger(String s);

}
