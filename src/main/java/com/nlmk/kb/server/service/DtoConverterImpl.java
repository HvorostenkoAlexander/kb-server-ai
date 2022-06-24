package com.nlmk.kb.server.service;

import com.nlmk.kb.server.dto.DictionaryConfigDto;
import com.nlmk.kb.server.dto.PdmMessageDto;
import com.nlmk.kb.server.entity.configurator.DictionaryConfig;
import com.nlmk.kb.server.entity.pdm.PdmMessage;
import com.nlmk.kb.server.service.CommonConverter;
import com.nlmk.kb.server.service.DtoConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DtoConverterImpl implements DtoConverter {

    private final CommonConverter converter;

    @Override
    public DictionaryConfig toDictionaryConfig(DictionaryConfigDto dto) {
        Assert.notNull(dto, "DictionaryConfigDto не должно быть null");

        final var entity = DictionaryConfig.builder()
                .id(dto.getId())
                .topic(dto.getTopic())
                .nsiPath(dto.getNsiPath())
                .enabled(dto.getEnabled())
                .build();
        if (dto.getCodes() != null) {
            entity.setCodes(
                    Arrays.stream(dto.getCodes()).collect(Collectors.toList())
            );
        }
        return entity;
    }

    @Override
    public DictionaryConfigDto toDictionaryConfigDto(DictionaryConfig entity) {
        Assert.notNull(entity, "DictionaryConfig не должно быть null");
        final var dto = DictionaryConfigDto.builder()
                .id(entity.getId())
                .enabled(entity.getEnabled())
                .nsiPath(entity.getNsiPath())
                .topic(entity.getTopic())
                .build();
        if (entity.getCodes() != null) {
            dto.setCodes(entity.getCodes().toArray(new Integer[0]));
        }
        return dto;
    }

    public PdmMessageDto toPdmMessageDto(PdmMessage entity) {
        final var dto = PdmMessageDto.builder()
                .id(entity.getId())
                .topic(entity.getTopic())
                .partition(entity.getPartition())
                .offset(entity.getOffset())
                .key(entity.getKey())
                .op(entity.getOp())
                .isPosted(entity.isPosted())
                .note(entity.getNote())
                .build();

        if (entity.getTs() != null) {
            dto.setTs(entity.getTs().toString());
        }
        if (entity.getDictionary() != null) {
            dto.setDictionary(entity.getDictionary().toString());
        }
        if (entity.getKbReceiptTs() != null) {
            dto.setKbReceiptTs(entity.getKbReceiptTs().toString());
        }
        return dto;
    }

}
