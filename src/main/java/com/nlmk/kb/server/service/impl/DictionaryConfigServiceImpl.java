package com.nlmk.kb.server.service.impl;

import com.nlmk.kb.server.dto.DictionaryConfigDto;
import com.nlmk.kb.server.repository.DictionaryConfigRepository;
import com.nlmk.kb.server.service.DictionaryConfigService;
import com.nlmk.kb.server.service.DtoConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class DictionaryConfigServiceImpl implements DictionaryConfigService {

    private final DictionaryConfigRepository repository;
    private final DtoConverter converter;

    @Override
    public Page<DictionaryConfigDto> findPyPage(PageRequest pageRequest) {
        return repository.findAll(pageRequest).map(converter::toDictionaryConfigDto);
    }

    @Override
    public DictionaryConfigDto findById(Long id) {
        return repository.findById(id).map(converter::toDictionaryConfigDto).orElseThrow(
                () -> new IllegalArgumentException(
                        String.format("Не найден объект с id: [%s]", id)
                )
        );
    }

    //todo совместно решить какой тип  @Transactional использовать.
    // org.springframework.transaction.annotation.Transactional vs javax.transaction.Transactional
    @Override
    @Transactional
    public DictionaryConfigDto update(Long id,
                                      @Valid DictionaryConfigDto dto) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException(
                    String.format("Не найден объект с id: [%s]", id)
            );
        }

        final var toSave = converter.toDictionaryConfig(dto);
        toSave.setId(id);
        return converter.toDictionaryConfigDto(repository.save(toSave));
    }

    @Override
    public void deleteById(long id) {
        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException erdae) {
            log.error(erdae.getMessage());
            throw new IllegalArgumentException(
                    String.format("Не найден объект с id: [%s]", id)
            );
        }
    }

    @Override
    public DictionaryConfigDto findByTopic(String topic) {
        Assert.notNull(topic, "topicName не должно быть null");

        final var entity = repository.findByTopic(topic).orElseThrow(
                () -> new IllegalArgumentException(
                        String.format("Не найден объект с topic: [%s]", topic)
                )
        );
        return converter.toDictionaryConfigDto(entity);
    }

    @Override
    public String getDictionaryUrlByTopic(String topic) {

        final var entity = findByTopic(topic);

        if (entity.getNsiPath() == null) {
            log.error("dictionaryUrl of DictionaryConfigDto is null, dto:[{}]",entity);

            throw new IllegalArgumentException(
                    String.format("dictionaryUrl of DictionaryConfigDto is null, dto:[%s]", entity)
            );
        }

        return entity.getNsiPath();
    }
}
