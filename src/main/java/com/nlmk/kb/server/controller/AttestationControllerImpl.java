package com.nlmk.kb.server.controller;

import com.nlmk.kb.server.api.ccm.kc.CcmKc1Request;
import com.nlmk.kb.server.api.ccm.kc.CcmKc1Response;
import com.nlmk.kb.server.api.ccm.kc.CcmKc2Request;
import com.nlmk.kb.server.api.ccm.kc.CcmKc2Response;
import com.nlmk.kb.server.api.ccm.pts.CcmPtsRequest;
import com.nlmk.kb.server.api.ccm.pts.CcmPtsResponse;
import com.nlmk.kb.server.service.AttestationMessageService;
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

    private final AttestationMessageService service;

    @Override
    public CcmPtsResponse postAttestationCcmPts(String requestId, CcmPtsRequest attRequest) {
        log.info("postAttestationCcmPts, CcmPtsRequest [{}]", attRequest);
        return service.ccmPtsRequestProcessing(attRequest);
    }

    @Override
    public CcmKc1Response postAttestationCcmKc1(String requestId, CcmKc1Request attRequest) {
        log.info("postAttestationCcmKc1, CcmKcRequest [{}]", attRequest);
        return service.ccmKc1RequestProcessing(attRequest);
    }

    @Override
    public CcmKc2Response postAttestationCcmKc2(String requestId, CcmKc2Request attRequest) {
        log.info("postAttestationCcmKc2, CcmKc2Request [{}]", attRequest);
        return service.ccmKc2RequestProcessing(attRequest);
    }

}
