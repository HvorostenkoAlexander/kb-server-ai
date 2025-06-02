package com.nlmk.kb.server.service;

import com.nlmk.attestation.product.api.nsi.SpApcsAttestationResultDto;
import com.nlmk.kb.server.service.client.NsiClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@ConditionalOnProperty(value = "service.scheduling.enable", havingValue = "true", matchIfMissing = true)
@EnableScheduling
public class AttestationResultsServiceImpl implements AttestationResultsService {

    private boolean isInitialUpdated;
    private final List<SpApcsAttestationResultDto> resultList;
    private final NsiClient nsiClient;

    public AttestationResultsServiceImpl(NsiClient client) {
        nsiClient = client;
        resultList = new ArrayList<>();
        isInitialUpdated = false;
    }

    @Override
    public synchronized Optional<SpApcsAttestationResultDto> getAttestationResultByCode(Integer code) {
        if (!isInitialUpdated) {
            updateAttestationResults();
        }
        return resultList.stream().filter(r -> r.getCode().equals(code)).findFirst();
    }

    @Scheduled(fixedRateString = "${service.scheduling.attestation-results}")
    public synchronized void updateAttestationResults() {
        resultList.clear();
        var results = nsiClient.getApcsAttestationResults();
        if (!results.isEmpty()) {
            resultList.addAll(results);
        }
        isInitialUpdated = true;
        log.info("updateAttestationResults, получено [{}] записей", resultList.size());
    }

}
