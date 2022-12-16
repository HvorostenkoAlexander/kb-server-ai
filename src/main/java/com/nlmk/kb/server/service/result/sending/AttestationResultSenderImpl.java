package com.nlmk.kb.server.service.result.sending;

import com.nlmk.attestation.product.api.ProductDto;
import com.nlmk.attestation.product.api.RequestDto;
import com.nlmk.attestation.product.api.pam.ProductAttestationResultDto;
import com.nlmk.kb.server.api.ResultsConfigDto;
import com.nlmk.kb.server.exception.AttestationResultSenderException;
import com.nlmk.kb.server.service.result.configuration.ResultConfigService;
import com.nlmk.kb.server.service.result.sending.adapter.ResultAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

@Slf4j
@Service
public class AttestationResultSenderImpl implements AttestationResultSender {

    private final Map<String, ResultAdapter<? extends SpecificRecordBase>> resultAdapters;
    private final ResultSender resultSender;
    private final ResultConfigService configService;
    private final CommonConditionFilter conditionFilter;

    public AttestationResultSenderImpl(List<ResultAdapter<? extends SpecificRecordBase>> allResultAdapters,
                                       ResultSender resultSender,
                                       ResultConfigService configService,
                                       CommonConditionFilter conditionFilter
    ) {
        this.resultAdapters = allResultAdapters.stream()
                .collect(
                toMap(ResultAdapter::getAvroName, Function.identity())
        );
        this.configService = configService;
        this.conditionFilter = conditionFilter;
        this.resultSender = resultSender;
    }

    @Override
    public void send(ProductAttestationResultDto productAttestationResult, Class<?> sendingType) {
        final var configs = configService.getEnabledTopics();
        if (configs.isEmpty()) {
            log.warn("send, конфигурации отправки результатов не найдены");
            return;
        }

        final var enabledAdapters = getEnabledAdapters(configs);
        if (enabledAdapters.isEmpty()) {
            log.error("send, адаптеры результата аттестации не найдены");
            throw new AttestationResultSenderException("send, результата аттестации не найдены");
        }

        final var product = productAttestationResult.getResult();
        log.info("send, результат аттестации для материала id [{}], referenceId [{}]", product.getId(), product.getReferenceId());

        for (ResultsConfigDto config : configs) {
            // только конфигурация своего типа!
            if (config.getAvroName().equals(sendingType.getSimpleName())) {
                log.info("конфигурация отправки результата: [{}]", config);
                sending(config,
                        enabledAdapters,
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
                         Set<ResultAdapter<?>> enabledAdapters,
                         ProductDto product,
                         boolean isNew) {
        // свой отправитель: по AvroName и совпадению AvroName с именем типа MessageProducer (от ошибок в базе)
        var adapter = enabledAdapters.stream()
                .filter(a -> a.getAvroName().equals(config.getAvroName()))
                .filter(a -> a.getSendingType().getSimpleName().equals(config.getAvroName()))
                .findFirst();

        if (adapter.isEmpty()) {
            log.warn("sending, не найден адаптер для топика [{}], avroName [{}]",
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

        final var results = adapter.get().adapt(product, isNew);

        var pk = adapter.get().getPk(results);

        if (Objects.isNull(results) || Objects.isNull(pk)) {
            throw new AttestationResultSenderException("produce, PK сообщения не найден");
        }

        final var key = StringUtils.joinWith("~", pk.getSystemCode(), pk.getId());

        resultSender.send(results, config.getTopic(), key);
    }

    /**
     * Уникальный список доступных адаптеров для списка активных конфигураций
     */
    private Set<ResultAdapter<?>> getEnabledAdapters(List<ResultsConfigDto> configs) {
        if (configs == null || configs.isEmpty()) {
            return Set.of();
        }

        // поиск по AvroName
        return configs.stream()
                .map(ResultsConfigDto::getAvroName)
                .filter(avroName -> resultAdapters.get(avroName) != null)
                .collect(toMap(k -> k, resultAdapters::get))
                .values().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());
    }

    private String getKceh(RequestDto request) {
        if (request.getKceh() != null) {
            return MessageFormat.format("значение request.kceh: {0}", request.getKceh());
        } else {
            return "null";
        }
    }

}
