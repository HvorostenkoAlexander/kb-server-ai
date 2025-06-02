package com.nlmk.kb.server.service;

import com.nlmk.attestation.product.api.nsi.SpApcsAttestationResultDto;

import java.util.Optional;

public interface AttestationResultsService {
    Optional<SpApcsAttestationResultDto> getAttestationResultByCode(Integer code);
    void updateAttestationResults();
}
