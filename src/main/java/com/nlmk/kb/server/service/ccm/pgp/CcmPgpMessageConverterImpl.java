package com.nlmk.kb.server.service.ccm.pgp;

import com.nlmk.kb.server.entity.CcmMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nlmk.l3.ccm.pgp.AttestationRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class CcmPgpMessageConverterImpl implements CcmPgpMessageConverter {

    private final AttestationRequestPgpConverter attestationRequestConverter;

    @Override
    public CcmMessage fromCcmAttestationRequest(AttestationRequest ccmAttestationRequest,
                                                String topic,
                                                String key,
                                                int partition,
                                                int offset,
                                                String timestamp) {

        final var value = attestationRequestConverter.toPamAttestationRequest(ccmAttestationRequest);

        final var requestMessage = CcmMessage.builder()
                .partition(partition)
                .offset(offset)
                .key(key)
                .topic(topic)
                .kbSendingTs(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
                .kbReceiptTs(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
                .request(value);

        if (value.getValue().getData() != null) {
            requestMessage.primeId(value.getValue().getData().getPrimeId());
        }
        return requestMessage.build();
    }

}
