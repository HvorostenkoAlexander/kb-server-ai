package com.nlmk.kb.server.controller;

import com.nlmk.kb.server.api.CcmPtsRequest;
import com.nlmk.kb.server.api.CcmPtsResponse;
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

    @Override
    public CcmPtsResponse postAttestationCcmPts(String requestId, CcmPtsRequest dto) {
        // fixme
        return CcmPtsResponse.builder().build();
    }

}
