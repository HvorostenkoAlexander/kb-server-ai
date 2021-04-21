package com.nlmk.kb.server.repository;

import com.nlmk.kb.server.entity.IntegralParam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IntegralParamRepository extends JpaRepository<IntegralParam,Long> {

    public List<IntegralParam> findIntegralParamByRecordPk(Integer recordPk);
}
