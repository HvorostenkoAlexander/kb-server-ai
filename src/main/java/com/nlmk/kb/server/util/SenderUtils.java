package com.nlmk.kb.server.util;

import com.nlmk.attestation.product.api.pam.AttestationRequest;
import com.nlmk.attestation.product.api.pam.DataField;
import com.nlmk.attestation.product.api.pam.DataPgp;
import com.nlmk.kb.server.config.KbConstants;
import java.util.Objects;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;

public final class SenderUtils {

    private SenderUtils() {
        throw new IllegalStateException("SenderUtils is util class");
    }

    public static void addRequestId(HttpHeaders headers) {
        if (Objects.isNull(headers)) {
            return;
        }

        headers.add(KbConstants.REQUEST_ID_HEADER,
                // готовый requestId или новый с префиксом
                Objects.requireNonNullElseGet(
                        MDC.get(KbConstants.REQUEST_ID_KEY), () -> KbConstants.DEFAULT_PREFIX + UUID.randomUUID()
                )
        );
    }

    public static String getPrimeId(AttestationRequest attRequest) {
        if (Objects.nonNull(attRequest)
                && Objects.nonNull(attRequest.getValue())
                && Objects.nonNull(attRequest.getValue().getData())) {

            return AdapterUtils.getDataField(attRequest.getValue().getData()).map(DataField::getPrimeId).orElse(null);
        }
        return null;
    }

    public static UUID getMetalUnitId(AttestationRequest attRequest) {
        if (Objects.nonNull(attRequest)
                && Objects.nonNull(attRequest.getValue())
                && Objects.nonNull(attRequest.getValue().getData())) {

            return AdapterUtils.getDataPgp(attRequest.getValue().getData()).map(DataPgp::getMetalUnitId).orElse(null);
        }
        return null;
    }
}
