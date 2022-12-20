package com.nlmk.kb.server.util;

import com.nlmk.attestation.product.api.SadimMessageDto;
import com.nlmk.attestation.product.api.pam.AttestationRequest;
import com.nlmk.attestation.product.api.pam.DataField;
import com.nlmk.kb.server.config.KbConstants;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;

import java.util.Objects;
import java.util.UUID;

public class SenderUtils {

    private SenderUtils() {
        throw new IllegalStateException("SenderUtils is util class");
    }

    public static void addRequestId(HttpHeaders headers) {
        if (Objects.isNull(headers)) {
            return;
        }

        // выбор варианта
        final var requestIdKafka = MDC.get(KbConstants.KAFKA_ID);
        final var requestIdRest = MDC.get(KbConstants.REQUEST_ID_KEY);
        final var requestId = (requestIdKafka != null) ? requestIdKafka : requestIdRest;

        headers.add(KbConstants.REQUEST_ID_HEADER,
                // готовый requestId или новый с префиксом
                Objects.requireNonNullElseGet(requestId, () -> KbConstants.DEFAULT_PREFIX + UUID.randomUUID()));
    }

    public static String getPrimeId(AttestationRequest attRequest) {
        if (Objects.nonNull(attRequest)
                && Objects.nonNull(attRequest.getValue())
                && Objects.nonNull(attRequest.getValue().getData())) {

            return AdapterUtils.getDataField(attRequest.getValue().getData()).map(DataField::getPrimeId).orElse(null);
        }
        return null;
    }

    public static String getPrimeId(SadimMessageDto dto) {
        if (Objects.isNull(dto) || Objects.isNull(dto.getParam())) {
            return null;
        }
        return dto.getParam().getPrimeId();
    }

}
