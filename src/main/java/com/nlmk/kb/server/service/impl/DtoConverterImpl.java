package com.nlmk.kb.server.service.impl;

import com.nlmk.attestation.product.api.PreAttestationParamDto;
import com.nlmk.kb.server.entity.PreAttestationParam;
import com.nlmk.kb.server.service.CommonConverter;
import com.nlmk.kb.server.service.DtoConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Slf4j
@Service
@RequiredArgsConstructor
public class DtoConverterImpl implements DtoConverter {

    private final CommonConverter converter;

    @Override
    public PreAttestationParamDto fromParamToDto(PreAttestationParam param) {
        Assert.notNull(param,"При конвертации в PreAttestationParamDto param = null.");

        val paramDto = PreAttestationParamDto.builder()
                .id(param.getId())
                .primeId(param.getPrimeId())
                .t12Min(param.getT12Min())
                .t12Max(param.getT12Max())
                .tcmMin(param.getTcmMin())
                .tcmMax(param.getTcmMax())
                .pbi(param.getPbi())
                .profFact(param.getProfFact())
                .wedgeFact(param.getWedgeFact())
                .sqcCritMax(param.getSqcCritMax())
                .ph1sgp(param.getPh1sgp())
                .ph12sgp(converter.parsToDouble(param.getPh12sgp()))
                .ph23sgp(param.getPh23sgp())
                .estimate(param.getEstimate())
                .build();
        if (param.getLclThckng() !=null){
            paramDto.setLclThckng(param.getLclThckng().toArray(new Double[0]));
        }
        return paramDto;
    }
}
