package com.nlmk.kb.server.service.result_config;

import nlmk.l3.apcs.VerificationResults;

public interface VerificationResultSender {

    void send(VerificationResults result, String topic);

}
