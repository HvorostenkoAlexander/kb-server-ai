package com.nlmk.kb.server.service.pdm;

import com.nlmk.kb.server.api.DictionaryConfigDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.validation.Valid;

public interface DictionaryConfigService {

    Page<DictionaryConfigDto> findPyPage(PageRequest pageRequest);

    DictionaryConfigDto findById(Long id);

    DictionaryConfigDto findByTopic(String topic);

    DictionaryConfigDto update(Long id, @Valid DictionaryConfigDto dto);

    void deleteById(long id);

    String getDictionaryUrlByTopic(String topic);

}
