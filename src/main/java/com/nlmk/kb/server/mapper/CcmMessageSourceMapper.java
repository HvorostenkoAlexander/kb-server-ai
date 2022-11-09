package com.nlmk.kb.server.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.kb.server.api.CcmMessageSourceDto;
import com.nlmk.kb.server.entity.CcmMessageSource;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface CcmMessageSourceMapper extends BaseMapper<CcmMessageSource, CcmMessageSourceDto> {

    @Named("messageSourceToDto")
    static JsonNode messageToDto(String messageSource) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(messageSource);
    }

    @Named("messageSourceFromDto")
    static String messageFromDto(JsonNode messageSource) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(messageSource);
    }

    @Mapping(source = "messageSource", target = "messageSource", qualifiedByName = "messageSourceToDto")
    @Override
    CcmMessageSourceDto toDto(CcmMessageSource ccmMessageSource);

    @Mapping(source = "messageSource", target = "messageSource", qualifiedByName = "messageSourceFromDto")
    @Override
    CcmMessageSource fromDto(CcmMessageSourceDto ccmMessageSource);

}
