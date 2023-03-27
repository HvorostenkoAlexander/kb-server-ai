package com.nlmk.kb.server.service.ccm;

import com.nlmk.kb.server.api.CcmMessageSourceDto;
import com.nlmk.kb.server.entity.CcmMessage;
import com.nlmk.kb.server.entity.CcmMessageSource;
import com.nlmk.kb.server.mapper.CcmMessageSourceMapper;
import com.nlmk.kb.server.repository.CcmMessageRepository;
import com.nlmk.kb.server.repository.CcmMessageSourceRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Slf4j
@Service
@RequiredArgsConstructor
public class CcmMessageServiceImpl implements CcmMessageService {

    private final CcmMessageRepository messageRepository;
    private final CcmMessageSourceRepository ccmMessageSourceRepository;

    private final CcmMessageSourceMapper sourceMapper;

    @Override
    @Transactional
    public Optional<CcmMessage> save(CcmMessage ccmMessage) {

        /*
        INFO:
        Одна запись, т.к. @UniqueConstraint(columnNames = {"topic", "partition", "msg_offset"})
        имеющаяся запись удаляется без чтения, потому что возможен устаревший формат json request
        */
        messageRepository.deleteOldByTopicAndPartitionAndOffset(ccmMessage.getTopic(),
                ccmMessage.getPartition(),
                ccmMessage.getOffset());

        var saved = messageRepository.save(ccmMessage);
        log.info("ccmMessage аттестации успешно сохранено для primeId: {}", saved.getPrimeId());

        return Optional.of(saved);
    }

    @Override
    public Page<CcmMessage> findAll(PageRequest of) {
        return messageRepository.findAll(of);
    }

    @Override
    public CcmMessage update(CcmMessage ccmMessage) {
        Assert.notNull(ccmMessage, "ccmMessage must not be null");

        return messageRepository.save(ccmMessage);
    }

    @Override
    public Optional<CcmMessage> findLastMessage(String primeId) {
        return messageRepository.findFirstByPrimeIdOrderByKbReceiptTsDesc(primeId);
    }

    @Override
    public Optional<CcmMessageSourceDto> findSourceMessageByRequestId(Long requestId) {
        return ccmMessageSourceRepository.findByRequestId(requestId).map(sourceMapper::toDto);
    }

    @Override
    public Optional<CcmMessageSourceDto> findSourceMessageByPrimeId(String primeId) {
        return ccmMessageSourceRepository.findFirstByPrimeIdOrderByCreatedAtDesc(primeId).map(sourceMapper::toDto);
    }

    @Override
    public void saveSourceMessage(Long requestId, String primeId, String ccmSourceMessageString, LocalDateTime createdAt) {
        ccmMessageSourceRepository.save(CcmMessageSource.builder()
                .requestId(requestId)
                .primeId(primeId)
                .createdAt(createdAt)
                .messageSource(ccmSourceMessageString).build());
    }

}
