package com.nlmk.kb.server.service.impl;

import com.nlmk.attestation.product.api.PreAttestationParamDto;
import com.nlmk.kb.server.entity.SadimMessage;
import com.nlmk.kb.server.exception.SadimJsonProcessingException;
import com.nlmk.kb.server.repository.SadimMessageRepository;
import com.nlmk.kb.server.service.DtoConverter;
import com.nlmk.kb.server.service.SadimJsonParser;
import com.nlmk.kb.server.service.SadimMessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class SadimMessageServiceImpl implements SadimMessageService {

    private final SadimJsonParser sadimJsonParser;
    private final SadimMessageRepository repository;
    private final DtoConverter converter;

    public SadimMessageServiceImpl(@Qualifier("sadimStreamApiParser") SadimJsonParser sadimJsonParser,
                                   SadimMessageRepository repository,
                                   DtoConverter converter) {
        this.sadimJsonParser = sadimJsonParser;
        this.repository = repository;
        this.converter = converter;
    }

    @Override
    @Transactional
    public SadimMessage saveMessage(ConsumerRecord consumerRecord) {
        Assert.notNull(consumerRecord, "consumerRecord must not be null");

        final var attestationParam = sadimJsonParser.getParam(consumerRecord.value().toString())
                .orElseThrow(
                        () -> new SadimJsonProcessingException("Не удалось получить параметры из сообщения от SADIM.")
                );

        log.debug("SADIM message with offset: {}; attestationParam:{}", consumerRecord.offset(), attestationParam);

        final var sadimMessage = SadimMessage.builder()
                .key(consumerRecord.key().toString())
                .partition(consumerRecord.partition())
                .offset(consumerRecord.offset())
                .ts(LocalDateTime.now())
                .param(attestationParam)
                .build();
        final var sadimFromBase = findByPartitionAndOffset(
                consumerRecord.partition(),
                consumerRecord.offset()
        );

        if (sadimFromBase.isPresent()) {
            log.warn("the message with offset: [{}]; partition: [{}] is already present in the database." +
                            " Loading data from base..."
                    , consumerRecord.offset(), consumerRecord.partition());

            return sadimFromBase.get();
        }
        return repository.save(sadimMessage);
    }

    private List<SadimMessage> findByParamPrimeId(String primeId) {
        Assert.notNull(primeId, "primeId must not be null");

        List<SadimMessage> messages = repository.findSadimMessagesByParam_PrimeIdOrderByTsDesc(primeId);
        log.warn("messages size by primeId: {}", messages.size());

        return messages;
    }

    private List<SadimMessage> findByParamMeltNoAndLotNo(Integer meltNo, Integer lotNo) {
        Assert.notNull(meltNo, "meltNo must not be null");
        Assert.notNull(lotNo, "lotNo must not be null");

        List<SadimMessage> messages = repository.findSadimMessagesByParam_MeltNoAndParam_LotNoOrderByTsDesc(meltNo, lotNo);
        log.warn("messages size by lotNo, meltNo: {}", messages.size());

        return messages;
    }

    private Optional<SadimMessage> findByPartitionAndOffset(Integer partition, Long offset) {
        Assert.notNull(partition, "partition must not be null");
        Assert.notNull(offset, "offset must not be null");

        return repository.findFirstByPartitionAndOffset(partition, offset);
    }

    @Override
    public PreAttestationParamDto findByAttesstationParam(String pkId,
                                                          String primeId,
                                                          Integer meltNo,
                                                          Integer lotNo) {
        validateParam(pkId, primeId);

        return batchFind(List.of(pkId, primeId), meltNo, lotNo)
                .orElseThrow(() -> {
                    var exceptionString = String.format("В базе данных kb-server на обнаружены данные с параметрами:" +
                                    " pkId:[%s]; primeId:[%s]; nplv(meltNo):[%s]; hnum(lotNo):[%s]",
                            pkId, primeId, meltNo, lotNo);
                    throw new IllegalArgumentException(exceptionString);
                });
    }

    private void validateParam(String pkId, String primeId) {
        if (StringUtils.isBlank(pkId) || StringUtils.isBlank(primeId)) {
            var exceptionString = String.format("Некорректные данные для запроса: pkId:[%s], primeId:[%s]",
                    pkId, primeId);
            throw new IllegalArgumentException(exceptionString);
        }
    }

    private Optional<PreAttestationParamDto> batchFind(List<String> identities,
                                                       Integer meltNo,
                                                       Integer lotNo) {
        Optional<PreAttestationParamDto> paramDto = Optional.empty();

        for (String id : identities) {
            paramDto = findByPrimeIdLatest(id);
            if (paramDto.isPresent()) {
                return paramDto;
            }
        }
        paramDto = findByMeltAndLotLatest(meltNo, lotNo);

        return paramDto;
    }

    private Optional<PreAttestationParamDto> findByPrimeIdLatest(String primeId) {
        final var messages = findByParamPrimeId(primeId);

        if (messages.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(converter.toPreAttestationParamDto(
                messages.get(0).getParam()
        ));
    }

    private Optional<PreAttestationParamDto> findByMeltAndLotLatest(Integer meltNo, Integer lotNo) {
        if (meltNo == null || lotNo == null) {
            return Optional.empty();
        }

        final var messages = findByParamMeltNoAndLotNo(meltNo, lotNo);

        if (messages.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(converter.toPreAttestationParamDto(
                messages.get(0).getParam()
        ));
    }

    private void updateMessageInBase(SadimMessage sadimFromBase, Integer lotNo, Integer meltNo) {
        final var preAttestationParam = sadimFromBase.getParam();

        if (preAttestationParam != null) {
            preAttestationParam.setLotNo(lotNo);
            preAttestationParam.setMeltNo(meltNo);

            log.info("UPDATE sadim message with offset: [{}];" +
                            " partition: [{}]," +
                            " PreAttestaionpParam: [{}];",
                    sadimFromBase.getOffset(), sadimFromBase.getPartition(),
                    sadimFromBase.getParam());
        }
    }
}
