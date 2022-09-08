package com.nlmk.kb.server.service.result.sending.pts;

import com.nlmk.attestation.product.api.ProductDto;
import com.nlmk.kb.server.service.result.configuration.ApcsAvro;
import com.nlmk.kb.server.service.result.sending.MessageProducer;
import com.nlmk.kb.server.service.result.sending.ResultAdapter;
import com.nlmk.kb.server.service.result.sending.ResultSenderPts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nlmk.l3.apcs.VerificationResultsPts;
import org.apache.avro.Schema;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PtsMessageProducerImpl implements ApcsAvro, MessageProducer<VerificationResultsPts> {

    private static final Schema SCHEMA = VerificationResultsPts.SCHEMA$;
    private final ResultAdapter<VerificationResultsPts> adapter;
    private final ResultSenderPts sender;

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
        log.info("produce product: id [{}], referenceId [{}] by AVRO name [{}] to topic [{}]",
                product.getId(), product.getReferenceId(), getSchemaName(), topic);

        final var results = adapter.adapt(product, isNew);
        sender.send(results, topic);
    }

    @Override
    public String getAvroName() {
        return getSchemaName();
    }

    @Override
    public Class<VerificationResultsPts> getSendingType() {
        return VerificationResultsPts.class;
    }

}
