package com.nlmk.kb.server.service.mes;

import com.nlmk.kb.server.entity.MesMessage;
import com.nlmk.kb.server.entity.MesMessageSource;
import com.nlmk.kb.server.repository.MesMessageRepository;
import com.nlmk.kb.server.repository.MesMessageSourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MesMessageServiceImpl implements MesMessageService {

    private final MesMessageRepository repository;
    private final MesMessageSourceRepository sourceRepository;

    @Override
    @Transactional
    public Optional<MesMessage> save(MesMessage mesMessage) {

        repository.deleteOldByTopicAndPartitionAndOffset(
                mesMessage.getTopic(),
                mesMessage.getPartition(),
                mesMessage.getOffset()
        );

        var saved = repository.save(mesMessage);
        log.info("ccmMessage аттестации успешно сохранено для metalUnitId: {}", saved.getMetalUnitId());

        return Optional.of(saved);
    }

    @Override
    public Page<MesMessage> findAll(PageRequest of) {
        return repository.findAll(of);
    }

    @Override
    public MesMessage update(MesMessage mesMessage) {
        Assert.notNull(mesMessage, "ccmMessage must not be null");
        return repository.save(mesMessage);
    }

    @Override
    public void saveSourceMessage(Long requestId, String primeId, String metalUnitId, String mesSourceMessageString, LocalDateTime createdAt) {
        sourceRepository.save(MesMessageSource.builder()
                .requestId(requestId)
                .primeId(primeId)
                .metalUnitId(metalUnitId)
                .messageSource(mesSourceMessageString)
                .createdAt(createdAt)
                .build());
    }

}
