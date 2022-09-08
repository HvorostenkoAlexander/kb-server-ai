package com.nlmk.kb.server.service.result.configuration;

import com.nlmk.kb.server.api.ResultsConfigDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public interface ResultConfigService {

    Page<ResultsConfigDto> findPyPage(PageRequest pageRequest);

    ResultsConfigDto findById(Integer id);

    ResultsConfigDto create(@NotNull @Valid ResultsConfigDto dto);

    ResultsConfigDto update(@NotNull @Valid ResultsConfigDto dto);

    /**
     * Получение всех активных конфигураций
     */
    List<ResultsConfigDto> getEnabledTopics();

    void deleteById(Integer id);

}
