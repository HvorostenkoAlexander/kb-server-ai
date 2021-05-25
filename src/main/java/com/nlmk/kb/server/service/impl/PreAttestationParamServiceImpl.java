package com.nlmk.kb.server.service.impl;

import com.nlmk.attestation.product.api.PreAttestationParamDto;
import com.nlmk.kb.server.entity.PreAttestationParam;
import com.nlmk.kb.server.repository.PreAttestationParamRepository;
import com.nlmk.kb.server.service.DtoConverter;
import com.nlmk.kb.server.service.PreAttestationParamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PreAttestationParamServiceImpl implements PreAttestationParamService {

    private final PreAttestationParamRepository repository;
    private final DtoConverter converter;

    @Override
    public Optional<PreAttestationParam> save(PreAttestationParam preAttestationParam) {
        return Optional.of(repository.save(preAttestationParam));
    }

    @Override
    public List<PreAttestationParamDto> findByPrimeId(String primeId){
        return repository.findByPrimeId(primeId).stream()
                .map(
                        p->converter.fromParamToDto(p)
                ).collect(Collectors.toList());
    }
}
