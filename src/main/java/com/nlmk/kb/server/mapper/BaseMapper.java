package com.nlmk.kb.server.mapper;

public interface BaseMapper<I, O> {
    O toDto(I in);
    I fromDto(O dto);
}