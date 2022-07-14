package com.nlmk.kb.server.service.result.sending;

import nlmk.l3.apcs.VerificationResults;

public interface VerificationResultSender {

    void send(VerificationResults result, String topic);

}
