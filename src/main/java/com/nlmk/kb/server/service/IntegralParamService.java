package com.nlmk.kb.server.service;

import com.nlmk.kb.server.entity.IntegralParam;

import java.util.List;

public interface IntegralParamService {

    public void save(IntegralParam integralParam);

    public List<IntegralParam> findByRecordPk(Integer recordPk);
}
