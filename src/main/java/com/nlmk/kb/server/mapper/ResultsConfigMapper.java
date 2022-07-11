package com.nlmk.kb.server.mapper;

import com.nlmk.kb.server.api.ResultsConfigDto;
import com.nlmk.kb.server.entity.ResultsConfig;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface ResultsConfigMapper extends BaseMapper<ResultsConfig, ResultsConfigDto> {
}
