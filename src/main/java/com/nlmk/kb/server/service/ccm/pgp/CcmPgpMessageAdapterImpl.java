package com.nlmk.kb.server.service.ccm.pgp;

import com.nlmk.kb.server.entity.CcmMessage;
import com.nlmk.kb.server.service.ccm.CcmMessageAdapter;
import com.nlmk.kb.server.service.ccm.KafkaRequestAdapter;
import java.io.IOException;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nlmk.l3.ccm.pgp.AttestationRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

@Component
@RequiredArgsConstructor
@Slf4j
public class CcmPgpMessageAdapterImpl implements CcmMessageAdapter<AttestationRequest> {

    private final KafkaRequestAdapter<AttestationRequest> adapter;

    @Override
    public Tuple2<CcmMessage, String> adapt(AttestationRequest requestMessage,
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
            ccmMessageBuilder.primeId(attestationRequest.getValue().getData().getPrimeId());
        }
        final var ccmMessage = ccmMessageBuilder.build();
        return Tuples.of(ccmMessage, getSourceMessage(requestMessage));
    }

    private String getSourceMessage(AttestationRequest requestMessage) {
        try {
            return requestMessage.toByteBuffer().toString();
        } catch (IOException e) {
            log.error("Ошибка преобразования сообщения в строку");
        }
        return StringUtils.EMPTY;
    }
}
