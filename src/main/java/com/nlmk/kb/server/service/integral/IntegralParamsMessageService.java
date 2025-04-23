package com.nlmk.kb.server.service.integral;

import com.nlmk.kb.server.entity.integral.IntegralParamsMessage;
import com.nlmk.kb.server.entity.integral.IntegralParamsResponse;

public interface IntegralParamsMessageService {

    /**
     * Сохранение интегральных параметров
     */
    IntegralParamsMessage save(IntegralParamsResponse integralParamsResponse);

}
