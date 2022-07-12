package com.nlmk.kb.server.service.result.configuration;

import com.nlmk.kb.server.api.ResultsConfigDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public interface ResultConfigService {

    Page<ResultsConfigDto> findPyPage(PageRequest pageRequest);

    ResultsConfigDto findById(Long id);

    ResultsConfigDto create(@NotNull @Valid ResultsConfigDto dto);

    ResultsConfigDto update(@NotNull @Valid ResultsConfigDto dto);

    List<ResultsConfigDto> getEnabledTopics();

    void deleteById(long id);

}
