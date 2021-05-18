package com.nlmk.kb.server.service.impl;

import com.nlmk.kb.server.entity.IntegralParam;
import com.nlmk.kb.server.repository.IntegralParamRepository;
import com.nlmk.kb.server.service.IntegralParamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class IntegralParamServiceImpl implements IntegralParamService {

    private final IntegralParamRepository integralParamRepository;

    @Override
    public void save(IntegralParam integralParam) {
        if (integralParam==null) {
            log.info("--- invalid integralParam: null");
        }
        integralParamRepository.save(integralParam);
        log.debug("--- Successfully saved integralParam: {}",integralParam);
    }

    @Override
    public List<IntegralParam> findByRecordPk(Integer recordPk) {
        return integralParamRepository.findIntegralParamByRecordPk(recordPk);
    }

    @Override
    public List<IntegralParam> findByDataPrimeId(String primeId) {
        return integralParamRepository.findIntegralParamByData_PrimeID(primeId);
    }
}
