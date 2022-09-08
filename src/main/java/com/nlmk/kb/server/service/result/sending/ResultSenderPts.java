package com.nlmk.kb.server.service.result.sending;

import nlmk.l3.apcs.VerificationResultsPts;

public interface ResultSenderPts {

    void send(VerificationResultsPts result, String topic);

}
