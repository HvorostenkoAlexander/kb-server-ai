package com.nlmk.kb.server.service;

import com.nlmk.kb.server.entity.pam.AttestationRequest;

public interface PamClientService {
    //todo изменить тип возвращаемого значения для сохранения результата запроса
    public void postAttestationRequest(AttestationRequest pamAttestetionRequest);
}
