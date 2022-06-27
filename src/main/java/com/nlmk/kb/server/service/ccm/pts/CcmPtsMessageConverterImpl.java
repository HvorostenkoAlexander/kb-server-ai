package com.nlmk.kb.server.service.ccm.pts;

import com.nlmk.kb.server.entity.CcmMessage;
import lombok.RequiredArgsConstructor;
import nlmk.l3.ccm.pts.AttestationRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class CcmPtsMessageConverterImpl implements CcmPtsMessageConverter {

    private final AttestationRequestPtsConverter attestationRequestConverter;

    @Override
    public CcmMessage fromCcmAttestationRequest(AttestationRequest ccmAttestationRequest,
                                                String topic,
                                                String key,
                                                int partition,
                                                int offset,
                                                String timestamp) {
        final var request = attestationRequestConverter.toPamAttestationRequest(ccmAttestationRequest);
        final var ts = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());

        final var ccmMessageBuilder = CcmMessage.builder()
                .partition(partition)
                .offset(offset)
                .key(key)
                .topic(topic)
                .kbSendingTs(ts)
                .kbReceiptTs(ts)
                .request(request);

        if (request.getValue().getData() != null) {
            ccmMessageBuilder.primeId(request.getValue().getData().getPrimeId());
        }
        return ccmMessageBuilder.build();
    }

}
