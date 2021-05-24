package com.nlmk.kb.server.service;

import com.nlmk.kb.server.entity.PreAttestationParam;

import java.util.Optional;

public interface PreAttestationParamService {

    Optional<PreAttestationParam> save(PreAttestationParam preAttestationParam);
}
