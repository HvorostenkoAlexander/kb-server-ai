package com.nlmk.kb.server.service.client;

import com.nlmk.attestation.product.api.nsi.SpApcsAttestationResultDto;
import com.nlmk.attestation.product.api.nsi.SpAttributeAttestationGroupDto;
import com.nlmk.attestation.product.api.nsi.SpAttributesDto;

import java.util.List;
import java.util.Optional;

public interface NsiClient {

    /**
     * Справочник групп аттестации
     *
     * @param id UUID группы аттестации
     * @return Группа аттестации
     */
    Optional<SpAttributeAttestationGroupDto> getAttributeAttestationGroup(String id, String requestId);

    /**
     * Справочник атрибутов
     *
     * @param id UUID атрибутов
     * @return Атрибуты
     */
    Optional<SpAttributesDto> getAttributes(String id, String requestId);

    /**
     * Справочник статусов аттестации
     *
     * @return Список статусов
     */
    List<SpApcsAttestationResultDto> getApcsAttestationResults();

}
