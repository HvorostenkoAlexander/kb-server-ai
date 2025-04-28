package com.nlmk.kb.server.service.ccm.pds;

import com.nlmk.kb.server.entity.CcmMessage;
import com.nlmk.kb.server.service.ccm.CcmMessageAdapter;
import com.nlmk.kb.server.service.ccm.KafkaRequestAdapter;
import com.nlmk.kb.server.util.SenderUtils;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CcmPdsMessageAdapterImpl implements CcmMessageAdapter<nlmk.l3.ccm.pds.AttestationRequest> {

    private final KafkaRequestAdapter<nlmk.l3.ccm.pds.AttestationRequest> adapter;

    @Override
    public CcmMessage adapt(nlmk.l3.ccm.pds.AttestationRequest requestMessage,
                            String topic,
                            String key,
                            int partition,
                            int offset) {
        final var attestationRequest = adapter.adapt(requestMessage);
        final var ts = new Date();

        final var ccmMessageBuilder = CcmMessage.builder()
                .partition(partition)
                .offset(offset)
                .key(key)
                .topic(topic)
                .kbSendingTs(ts)
                .kbReceiptTs(ts)
                .request(attestationRequest);

        if (attestationRequest.getValue().getData() != null) {
            ccmMessageBuilder.primeId(
                    SenderUtils.getPrimeId(attestationRequest)
            );
        }
        return ccmMessageBuilder.build();
    }
}