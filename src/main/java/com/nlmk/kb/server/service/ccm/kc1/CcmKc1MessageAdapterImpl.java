package com.nlmk.kb.server.service.ccm.kc1;

import com.nlmk.kb.server.entity.CcmMessage;
import com.nlmk.kb.server.service.ccm.CcmMessageAdapter;
import com.nlmk.kb.server.service.ccm.KafkaRequestAdapter;
import com.nlmk.kb.server.util.SenderUtils;
import lombok.RequiredArgsConstructor;
import nlmk.nlmk.l3.sus.kc1.DbAttestRequestVer0;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CcmKc1MessageAdapterImpl implements CcmMessageAdapter<DbAttestRequestVer0> {

    private final KafkaRequestAdapter<DbAttestRequestVer0> adapter;

    @Override
    public CcmMessage adapt(DbAttestRequestVer0 requestMessage,
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

        if (Objects.nonNull(attestationRequest.getValue().getData())) {
            ccmMessageBuilder.primeId(
                    SenderUtils.getPrimeId(attestationRequest)
            );
        }
        return ccmMessageBuilder.build();
    }

}
