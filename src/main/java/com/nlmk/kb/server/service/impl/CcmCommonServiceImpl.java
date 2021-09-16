package com.nlmk.kb.server.service.impl;

import com.nlmk.kb.server.entity.CcmAttestationRequestMessage;
import com.nlmk.kb.server.service.CcmCommonService;
import com.nlmk.kb.server.service.CcmMessageService;
import com.nlmk.kb.server.service.CcmPamClientSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Comparator;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class CcmCommonServiceImpl implements CcmCommonService {

    private final CcmPamClientSender ccmPamSender;
    private final CcmMessageService messageService;

    @Override
    public void rePostAttestation(String primeId) {
        if (StringUtils.isBlank(primeId)) {
            log.warn("Невозможно осуществить повторную отправку. primeId is null.");
            return;
        }
        final var requests = messageService.findByPrimeId(primeId);

        if (requests.isEmpty()) {
            log.warn("Для повторной отправки в базе данных kb-server не обаружены запросы на аттестацию с primeId: [{}]", primeId);
            return;
        }

        if (requests.size() > 1) {
            log.warn("В базе данных kb-server обаружено [{}] запроса на аттестацию с primeId: [{}]",
                    requests.size(), primeId);
            requests.stream().map(
                    r -> "id: " + r.getId() + " ts: " + r.getKafkaTs() + "; primeId: " + r.getPrimeId()
            ).forEach(log::debug);
        }

        try {
            requests.stream().sorted(
                    Comparator.comparing(CcmAttestationRequestMessage::getKafkaTs)
                            .reversed()
            ).findFirst().ifPresent(this::rePostRequest);
        } catch (Exception ex) {
            log.error("Ошибка при повторной передачи запроса на аттестацию: [{}]", ex.getMessage());
        }
    }

    @Override
    public void postAttestation(CcmAttestationRequestMessage request) {
        Assert.notNull(request, "request is null");

        final var savedRequest = messageService.save(request).orElseThrow(
                () -> new RuntimeException("Не удалось сохранить сообщение partition: " + request.getPartition()
                        + "; offset: " + request.getOffset())
        );

        if (request.getRequest() != null ||
                request.getRequest().getValue() != null ||
                request.getRequest().getValue().getData() != null) {

            postRequest(savedRequest, "recived");

        } else {
            log.warn("В поступившем запросе на аттестацию нет данных. Отправка невозможна.");
        }
    }

    private void rePostRequest(CcmAttestationRequestMessage r) {
        log.info("Повторная отправка запроса на аттестацию. primeId: [{}], ts:[{}]", r.getPrimeId(), r.getKafkaTs());
        postRequest(r, "re-recived");
    }

    private void postRequest(CcmAttestationRequestMessage r, String statusNote) {
        final var pamResult = ccmPamSender.postAttestationRequest(r.getRequest());
        if (pamResult != null) {
            r.setStatus(statusNote);
            r.setKafkaTs(new Date());
            messageService.update(r);
        }
    }
}
