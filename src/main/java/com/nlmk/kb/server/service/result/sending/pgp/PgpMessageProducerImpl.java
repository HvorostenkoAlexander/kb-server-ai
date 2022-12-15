package com.nlmk.kb.server.service.result.sending.pgp;

import com.nlmk.attestation.product.api.ProductDto;
import com.nlmk.kb.server.exception.AttestationResultSenderException;
import com.nlmk.kb.server.service.result.configuration.ApcsAvro;
import com.nlmk.kb.server.service.result.sending.MessageProducer;
import com.nlmk.kb.server.service.result.sending.ResultAdapter;
import com.nlmk.kb.server.service.result.sending.ResultSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nlmk.l3.apcs.VerificationResults;
import org.apache.avro.Schema;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class PgpMessageProducerImpl implements ApcsAvro, MessageProducer<VerificationResults> {

    private static final Schema SCHEMA = VerificationResults.SCHEMA$;
    private final ResultAdapter<VerificationResults> adapter;
    private final ResultSender sender;

    @Override
    public String getSchemaName() {
        return SCHEMA.getName();
    }

    @Override
    public String getSchemaDoc() {
        return SCHEMA.getDoc();
    }

    @Override
    public String getSchemaData() {
        return SCHEMA.toString(false);
    }

    @Override
    public void produce(ProductDto product, boolean isNew, String topic) {
        log.info("produce, id [{}], referenceId [{}] AVRO name [{}] topic [{}]",
                product.getId(), product.getReferenceId(), getSchemaName(), topic);

        if (StringUtils.isBlank(topic)) {
            throw new AttestationResultSenderException("produce, топик для отправки сообщения не задан");
        }

        final var results = adapter.adapt(product, isNew);

        if (Objects.isNull(results) || Objects.isNull(results.getPk())) {
            throw new AttestationResultSenderException("produce, PK сообщения не найден");
        }

        final var key = StringUtils.joinWith("~", results.getPk().getSystemCode(), results.getPk().getId());

        sender.send(results, topic, key);
    }

    @Override
    public String getAvroName() {
        return getSchemaName();
    }

    @Override
    public Class<VerificationResults> getSendingType() {
        return VerificationResults.class;
    }

}
