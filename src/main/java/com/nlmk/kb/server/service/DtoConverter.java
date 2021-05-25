package com.nlmk.kb.server.service;

import com.nlmk.attestation.product.api.PreAttestationParamDto;
import com.nlmk.kb.server.entity.PreAttestationParam;

public interface DtoConverter {

    public PreAttestationParamDto fromParamToDto(PreAttestationParam param);
}
