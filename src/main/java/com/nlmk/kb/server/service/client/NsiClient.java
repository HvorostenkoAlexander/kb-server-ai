package com.nlmk.kb.server.service.client;

import com.nlmk.attestation.product.api.nsi.SpAttributeAttestationGroupDto;
import com.nlmk.attestation.product.api.nsi.SpAttributesDto;

import java.util.Optional;

public interface NsiClient {

    /**
     * Справочник групп аттестации
     *
     * @param id UUID группы аттестации
     * @return Группа аттестации
     */
    Optional<SpAttributeAttestationGroupDto> getAttributeAttestationGroup(String id, String primeId);

    /**
     * Справочник атрибутов
     *
     * @param id UUID атрибутов
     * @return Атрибуты
     */
    Optional<SpAttributesDto> getAttributes(String id, String primeId);

}
