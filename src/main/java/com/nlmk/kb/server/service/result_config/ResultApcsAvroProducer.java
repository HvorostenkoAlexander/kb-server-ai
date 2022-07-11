package com.nlmk.kb.server.service.result_config;

import com.nlmk.attestation.product.api.ProductDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nlmk.l3.apcs.VerificationResults;
import org.apache.avro.Schema;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResultApcsAvroProducer implements ApcsAvro, MessageProducer {

    private static final Schema SCHEMA = VerificationResults.SCHEMA$;
    private final VerificationResultsAdapter adapter;
    private final VerificationResultSender sender;

    @Override
    public String getName() {
        return SCHEMA.getName();
    }

    @Override
    public String getDescription() {
        return SCHEMA.getDoc();
    }

    @Override
    public String getData() {
        return SCHEMA.toString(false);
    }

    @Override
    public void produce(ProductDto product, boolean isNew, String topic) {
        log.info("Отправка продукта: [{}] по схеме: [{}], класса VerificationResults в топик: [{}]",
                product, getDescription(), topic);

        VerificationResults results = adapter.adapt(product, isNew);
        sender.send(results, topic);
    }

    @Override
    public String getType() {
        return getDescription();
    }

}
