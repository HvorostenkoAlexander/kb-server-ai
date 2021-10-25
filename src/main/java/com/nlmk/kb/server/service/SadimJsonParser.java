package com.nlmk.kb.server.service;

import com.nlmk.kb.server.entity.PreAttestationParam;

import java.util.Optional;

public interface SadimJsonParser {

    /**
     * Создание объекта класса PreAttestationParam из строки в формате json
     *
     * @param jsonString
     * @return объект Optional<PreAttestationParam>.
     * Если не удается получить объект Optional.isEmpty
     */


    public Optional<PreAttestationParam> getParam(String jsonString);
}
