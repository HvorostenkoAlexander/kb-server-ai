package com.nlmk.kb.server.service.ccm;

import com.nlmk.attestation.product.api.pam.ProductAttestationResultDto;
import com.nlmk.kb.server.entity.CcmMessage;

public interface CcmCommonService {

    ProductAttestationResultDto rePostAttestation(String primeId) throws IllegalArgumentException;

    void postAttestation(CcmMessage request);

}
