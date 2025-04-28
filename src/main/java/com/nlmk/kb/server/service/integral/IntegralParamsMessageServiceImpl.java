package com.nlmk.kb.server.service.integral;

import com.nlmk.kb.server.entity.integral.IntegralParamsMessage;
import com.nlmk.kb.server.entity.integral.IntegralParamsResponse;
import com.nlmk.kb.server.repository.IntegralParamsMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class IntegralParamsMessageServiceImpl implements IntegralParamsMessageService {

    private final IntegralParamsMessageRepository integralParamsMessageRepository;

    @Override
    @Transactional
    public IntegralParamsMessage save(IntegralParamsResponse integralParamsResponse) {
        var message = IntegralParamsMessage.builder()
                .primeId(integralParamsResponse.getMetalUnitId())
                .response(integralParamsResponse)
                .build();

        return integralParamsMessageRepository.save(message);
    }
}
