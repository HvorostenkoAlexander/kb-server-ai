package com.nlmk.kb.server.mapper;

import com.nlmk.kb.server.api.ReimportDto;
import com.nlmk.kb.server.entity.Reimport;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReimportMapper extends BaseMapper<Reimport, ReimportDto> {
}

