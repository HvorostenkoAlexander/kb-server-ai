package com.nlmk.kb.server.service.integral;

import com.nlmk.attestation.product.api.RequestSource;
import com.nlmk.kb.server.entity.integral.IntegralParamsMessage;
import com.nlmk.kb.server.entity.integral.IntegralParamsResponse;
import com.nlmk.kb.server.repository.IntegralParamsMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class IntegralParamsMessageServiceImpl implements IntegralParamsMessageService {

    private final IntegralParamsMessageRepository integralParamsMessageRepository;

    @Override
    @Transactional
    public IntegralParamsMessage upsert(IntegralParamsResponse integralParamsResponse, RequestSource source) {

        if (RequestSource.CCM.equals(source)) {
            integralParamsMessageRepository.deleteByPrimeId(integralParamsResponse.getMetalUnitId());
        } else {
            integralParamsMessageRepository.deleteByMetalUnitId(UUID.fromString(integralParamsResponse.getMetalUnitId()));
        }

        var paramsMessageBuilder = IntegralParamsMessage.builder();
        paramsMessageBuilder.response(integralParamsResponse);

        if (RequestSource.CCM.equals(source)) {
            paramsMessageBuilder.primeId(integralParamsResponse.getMetalUnitId());
        } else {
            paramsMessageBuilder.metalUnitId(UUID.fromString(integralParamsResponse.getMetalUnitId()));
        }

        return integralParamsMessageRepository.save(paramsMessageBuilder.build());
    }
}
