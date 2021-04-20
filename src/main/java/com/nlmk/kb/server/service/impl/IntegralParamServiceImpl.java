package com.nlmk.kb.server.service.impl;

import com.nlmk.kb.server.entity.IntegralParam;
import com.nlmk.kb.server.repository.IntegralParamRepository;
import com.nlmk.kb.server.service.IntegralParamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
        log.info("--- Successfully saved integralParam: {}",integralParam);
    }
}
