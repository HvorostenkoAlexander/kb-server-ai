package com.nlmk.kb.server.service;

import com.nlmk.kb.server.dto.DictionaryConfigDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public interface DictionaryConfigService {

    Page<DictionaryConfigDto> findPyPage(PageRequest pageRequest);

    DictionaryConfigDto findById(Long id);

    DictionaryConfigDto update(Long id,
                               @Valid DictionaryConfigDto dto);

    void deleteById(long id);
}
