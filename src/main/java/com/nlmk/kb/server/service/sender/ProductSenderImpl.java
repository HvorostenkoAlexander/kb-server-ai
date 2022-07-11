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
        final var configs = configService.getEnabledTopics();
        if (configs.isEmpty()) {
            log.warn("В конфигурационной таблице не установлены топики для отправки сообщений в Kafka-Rest.");
            return;
        }

        final var enabledSenders = getEnabledSenders(configs);
        if (enabledSenders.isEmpty()) {
            log.error("Пустой список отправителей для активных топиков");
            throw new ProductSenderException("Нет зарегистрированных отправителей для сообщений в активные топики.");
        }

        final var product = productAttestationResult.getResult();
        final var isNew = productAttestationResult.isNewProduct();
        log.info("send attestation result for product: id [{}], referenceId [{}]", product.getId(), product.getReferenceId());

        for (ResultsConfigDto config : configs) {
            var sender = enabledSenders.stream()
                    .filter(s -> s.getType().equals(config.getAvroName()))
                    .findFirst();

            if (sender.isEmpty()) {
                log.warn("Для топика: [{}], не зарегистрирован отправитель с avroName: [{}]",
                        config.getTopic(), config.getAvroName());
                continue;
            }

            ProductDto sendingProduct = null;
            if (config.getCondition() != null) {
                final var sendingProductOpt = conditionFilter.filter(product, config.getCondition());

                if (sendingProductOpt.isEmpty()) {
                    log.warn("send, sending product after filter is EMPTY");
                    return;
                }
                sendingProduct = sendingProductOpt.get();

                log.info("sendingProduct after filter: [{}],", sendingProduct);

                if (sendingProduct.getRequests() != null) {
                    sendingProduct.getRequests().stream().map(this::getKceh).forEach(log::info);
                }
            }

            if (sendingProduct == null || sendingProduct.getRequests().isEmpty()) {
                log.warn("В полученном результате нет сведений отвечающих условиям: [{}]", config.getCondition());
                return;
            }

            // отправка результата
            sender.get().produce(sendingProduct, isNew, config.getTopic());
        }
    }

    private List<MessageProducer> getEnabledSenders(List<ResultsConfigDto> configs) {
        if (configs == null || configs.isEmpty()) {
            return List.of();
        }

        return configs.stream()
                .map(ResultsConfigDto::getAvroName)
                .collect(Collectors.toMap(k -> k, senders::get))
                .values().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableList());
    }

    private String getKceh(RequestDto request) {
        if (request.getKceh() != null) {
            return MessageFormat.format("Значение request.kceh: {0}", request.getKceh());
        } else {
            return "null";
        }
    }

}
