package com.nlmk.kb.server.controller;

import com.nlmk.kb.server.api.CcmPtsRequest;
import com.nlmk.kb.server.api.CcmPtsResponse;
import com.nlmk.kb.server.service.sender.PamSender;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@Timed(percentiles = {0.99, 0.95})
@CrossOrigin(origins = "*", methods = {RequestMethod.OPTIONS, RequestMethod.POST})
public class AttestationControllerImpl implements AttestationController {

    private final PamSender pamSender;

    @Override
    public CcmPtsResponse postAttestationCcmPts(String requestId, CcmPtsRequest attRequest) {
        /*
         * 1. принять запрос на аттестацию
         * 2. преобразовать запрос к общему виду для PAM (com.nlmk.kb.server.entity.pam.AttestationRequest)
         * 3. сохранить в базу (для запуска повторной аттестации по сообщениям САДиМ)
         * (нельзя одновременно работать с запросами на Аттестацию через Kafka и REST
         *  т.к. нужно искать сообщение в базе по primeId: структуры разные, таблицы разные где искать, в какой таблице?)
         * 4. отправить запрос в PAM
         * 5. получить ответ
         * 6. преобразовать ответ и отправить
         *  */
        // fixme
        return CcmPtsResponse.builder().build();
    }

}
