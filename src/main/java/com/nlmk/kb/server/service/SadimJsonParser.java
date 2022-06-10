package com.nlmk.kb.server.service;

import com.nlmk.attestation.product.api.SadimMessageDto;

import java.util.Optional;

public interface SadimJsonParser {

    /**
     * Создание объекта с параметрами пред аттестации из строки в формате json
     *
     * @param jsonString строка сообщения
     * @return объект <code>SadimMessageDto.ParamDto</code>. Если не удается, то <code>Optional.isEmpty</code>.
     */
    Optional<SadimMessageDto.ParamDto> getParam(String jsonString);

}
