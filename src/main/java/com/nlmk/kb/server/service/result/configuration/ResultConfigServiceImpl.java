package com.nlmk.kb.server.service.result.configuration;

import com.nlmk.kb.server.api.ResultsConfigDto;
import com.nlmk.kb.server.entity.ResultsConfig;
import com.nlmk.kb.server.mapper.ResultsConfigMapper;
import com.nlmk.kb.server.repository.ResultsConfigRepository;
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
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class ResultConfigServiceImpl implements ResultConfigService {

    private final ResultsConfigRepository repository;
    private final ResultsConfigMapper resultsConfigMapper;

    @Override
    public Page<ResultsConfigDto> findPyPage(PageRequest pageRequest) {
        Assert.notNull(pageRequest, "pageRequest must not be null");

        return repository.findAll(pageRequest)
                .map(resultsConfigMapper::toDto);
    }

    @Override
    public ResultsConfigDto findById(Long id) {
        Assert.notNull(id, "id must not be null");

        final var vrcConfig = repository.findById(id).orElseThrow(
                () -> new IllegalArgumentException(
                        String.format("Не найден объект с id: [%s]", id
                        ))
        );
        return resultsConfigMapper.toDto(vrcConfig);
    }

    @Override
    public ResultsConfigDto create(@NotNull @Valid ResultsConfigDto dto) {
        Assert.isNull(dto.getId(), "При создании нового объекта id должен быть null");
        return save(dto);
    }

    @Override
    @Transactional
    public ResultsConfigDto update(@NotNull @Valid ResultsConfigDto dto) {
        Assert.notNull(dto.getId(), "При изменении объекта id должен быть не null");
        findById(dto.getId());

        return save(dto);
    }

    @Override
    public List<ResultsConfigDto> getEnabledTopics() {
        return repository.findAll().stream()
                .filter(ResultsConfig::isEnabled)
                .map(resultsConfigMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(long id) {
        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException erdae) {
            log.warn(erdae.getMessage());
            throw new IllegalArgumentException("Не найден объект с id: " + id);
        }
    }

    private ResultsConfigDto save(ResultsConfigDto dto) {
        final var saved = repository.save(resultsConfigMapper.fromDto(dto));
        return resultsConfigMapper.toDto(saved);
    }

}
