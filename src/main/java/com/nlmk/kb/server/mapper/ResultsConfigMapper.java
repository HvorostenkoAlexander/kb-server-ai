package com.nlmk.kb.server.mapper;

import com.nlmk.kb.server.api.ResultsConfigDto;
import com.nlmk.kb.server.entity.ResultsConfig;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ResultsConfigMapper extends BaseMapper<ResultsConfig, ResultsConfigDto> {
}
