package com.nlmk.kb.server.service.sender;

import com.nlmk.attestation.product.api.pam.ProductAttestationResultDto;
import com.nlmk.kb.server.service.result.sending.CommonConditionFilter;
import com.nlmk.kb.server.service.result.sending.MessageProducer;
import com.nlmk.kb.server.service.result.configuration.ResultConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Slf4j
@Service
public class ProductSenderImpl implements ProductSender {

    private final Map<String, MessageProducer> senders;
    private final ResultConfigService configService;
    private final CommonConditionFilter conditionFilter;

    public ProductSenderImpl(List<MessageProducer> allSenders,
                             ResultConfigService configService,
                             CommonConditionFilter conditionFilter) {
        this.senders = allSenders.stream().collect(
                toMap(MessageProducer::getType, Function.identity())
        );
        this.configService = configService;
        this.conditionFilter = conditionFilter;
    }

    @Override
    public void send(ProductAttestationResultDto productAttestationResult) {
        final var isNew = productAttestationResult.isNewProduct();
        final var product = productAttestationResult.getResult();

        log.info("send attestation result for product: id [{}], referenceId [{}]", product.getId(), product.getReferenceId());

        // todo
    }

}
