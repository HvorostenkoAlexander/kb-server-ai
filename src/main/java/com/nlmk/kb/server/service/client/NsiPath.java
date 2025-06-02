package com.nlmk.kb.server.service.client;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NsiPath {

    GET_ATTRIBUTE_ATTESTATION_GROUP("/nsi/dict/mdm/sp_attribute_attestation_group"),
    GET_ATTRIBUTES("/nsi/dict/mdm/sp_attributes"),
    GET_APCS_ATTESTATION_RESULT("/nsi/dict/mdm/sp_apcs_attestation_result");

    private final String value;
}
