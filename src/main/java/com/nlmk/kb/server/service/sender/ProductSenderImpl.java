package com.nlmk.kb.server.service.sender;

import com.nlmk.attestation.product.api.ProductDto;
import com.nlmk.attestation.product.api.RequestDto;
import com.nlmk.attestation.product.api.pam.ProductAttestationResultDto;
import com.nlmk.kb.server.api.ResultsConfigDto;
import com.nlmk.kb.server.exception.ProductSenderException;
import com.nlmk.kb.server.service.result.sending.CommonConditionFilter;
import com.nlmk.kb.server.service.result.sending.MessageProducer;
import com.nlmk.kb.server.service.result.configuration.ResultConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

@Slf4j
@Service
public class ProductSenderImpl implements ProductSender {

    private final Map<String, MessageProducer<?>> senders;
    private final ResultConfigService configService;
    private final CommonConditionFilter conditionFilter;

    public ProductSenderImpl(List<MessageProducer<?>> allSenders,
                             ResultConfigService configService,
                             CommonConditionFilter conditionFilter) {
        this.senders = allSenders.stream().collect(
                toMap(MessageProducer::getAvroName, Function.identity())
        );
        this.configService = configService;
        this.conditionFilter = conditionFilter;
    }

    @Override
    public void send(ProductAttestationResultDto productAttestationResult, Class<?> sendingType) {
        final var configs = configService.getEnabledTopics();
        if (configs.isEmpty()) {
            log.warn("send, empty enabled topic config FOR sending result");
            return;
        }

        final var enabledSenders = getEnabledSenders(configs);
        if (enabledSenders.isEmpty()) {
            log.error("send, empty enabled sender list");
            throw new ProductSenderException("send, empty enabled sender list");
        }

        final var product = productAttestationResult.getResult();
        log.info("send attestation result for product: id [{}], referenceId [{}]", product.getId(), product.getReferenceId());

        for (ResultsConfigDto config : configs) {
            // только конфигурация своего типа!
            if (config.getAvroName().equals(sendingType.getSimpleName())) {
                log.info("sending config [{}]", config);
                sending(config,
                        enabledSenders,
                        product,
                        productAttestationResult.isNewProduct()
                );
            }
        }
    }

    /**
     * Отправка по одной активной конфигурации
     */
    private void sending(ResultsConfigDto config,
                         Set<MessageProducer<?>> enabledSenders,
                         ProductDto product,
                         boolean isNew) {
        // свой отправитель: по AvroName и совпадению AvroName с именем типа MessageProducer (от ошибок в базе)
        var sender = enabledSenders.stream()
                .filter(producer -> producer.getAvroName().equals(config.getAvroName()))
                .filter(producer -> producer.getSendingType().getSimpleName().equals(config.getAvroName()))
                .findFirst();

        if (sender.isEmpty()) {
            log.warn("sending, for topic [{}] not found sender with avroName [{}] (avroName and sendingType)",
                    config.getTopic(), config.getAvroName());
            return;
        }

        ProductDto sendingProduct = product;
        if (config.getCondition() != null) {
            final var sendingProductOpt = conditionFilter.filter(product, config.getCondition());

            if (sendingProductOpt.isEmpty()) {
                log.warn("sending, sending product after filter is EMPTY");
                return;
            }
            sendingProduct = sendingProductOpt.get();

            log.info("sending, sendingProduct after filter: [{}],", sendingProduct);

            if (sendingProduct.getRequests() != null) {
                sendingProduct.getRequests().stream().map(this::getKceh).forEach(log::info);
            }
        }

        if (sendingProduct == null
                || sendingProduct.getRequests() == null
                || sendingProduct.getRequests().isEmpty()) {
            log.warn("sending, empty data after condition [{}]", config.getCondition());
            return;
        }

        // отправка результата
        sender.get().produce(sendingProduct, isNew, config.getTopic());
    }

    /**
     * Уникальный список доступных отправителей для списка активных конфигураций
     */
    private Set<MessageProducer<?>> getEnabledSenders(List<ResultsConfigDto> configs) {
        if (configs == null || configs.isEmpty()) {
            return Set.of();
        }

        // поиск MessageProducer по AvroName
        return configs.stream()
                .map(ResultsConfigDto::getAvroName)
                .filter(avroName -> senders.get(avroName) != null)
                .collect(toMap(k -> k, senders::get))
                .values().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());
    }

    private String getKceh(RequestDto request) {
        if (request.getKceh() != null) {
            return MessageFormat.format("value of request.kceh: {0}", request.getKceh());
        } else {
            return "null";
        }
    }

}
