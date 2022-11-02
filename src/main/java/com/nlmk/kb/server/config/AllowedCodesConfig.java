package com.nlmk.kb.server.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class AllowedCodesConfig {

    private final String allowedAnalysisCodes;

    private final String allowedMechanicalCodes;

    public AllowedCodesConfig(@Value("${request.ccm.pts.allowedAnalysisCodes}") String allowedAnalysisCodes,
                              @Value("${request.ccm.pts.allowedMechanicalCodes}") String allowedMechanicalCodes) {
        this.allowedAnalysisCodes = allowedAnalysisCodes;
        this.allowedMechanicalCodes = allowedMechanicalCodes;
    }

    @Bean
    AllowedCodesConfig getAllowedCodesConfig() {
        return this;
    }
}
