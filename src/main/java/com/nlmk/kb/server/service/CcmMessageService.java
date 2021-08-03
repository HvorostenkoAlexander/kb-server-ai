package com.nlmk.kb.server.service;

import com.nlmk.kb.server.entity.CcmAttestationRequestMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

public interface CcmMessageService {

    public Optional<CcmAttestationRequestMessage> save(CcmAttestationRequestMessage ccmMessage);

    Page<CcmAttestationRequestMessage> findAll(PageRequest of);

    List<CcmAttestationRequestMessage> findByPrimeId(String primeId);

    CcmAttestationRequestMessage update(CcmAttestationRequestMessage ccmMessage);
}
