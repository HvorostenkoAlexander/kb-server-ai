package com.nlmk.kb.server.service.impl;

import com.nlmk.kb.server.entity.PreAttestationParam;
import com.nlmk.kb.server.repository.PreAttestationParamRepository;
import com.nlmk.kb.server.service.PreAttestationParamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PreAttestationParamServiceImpl implements PreAttestationParamService {

    private final PreAttestationParamRepository repository;

    @Override
    public Optional<PreAttestationParam> save(PreAttestationParam preAttestationParam) {

        return Optional.of(repository.save(preAttestationParam));
    }
}
