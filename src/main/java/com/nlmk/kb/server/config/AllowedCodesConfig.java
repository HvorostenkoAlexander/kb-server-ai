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

    private final String allowedPropertyAttributes;
    public AllowedCodesConfig(@Value("${request.ccm.pts.allowedAnalysisCodes}") String allowedAnalysisCodes,
                              @Value("${request.ccm.pts.allowedMechanicalCodes}") String allowedMechanicalCodes,
                              @Value("${request.ccm.pts.allowedPropertyAttributes}") String allowedPropertyAttributes) {
        this.allowedAnalysisCodes = allowedAnalysisCodes;
        this.allowedMechanicalCodes = allowedMechanicalCodes;
        this.allowedPropertyAttributes = allowedPropertyAttributes;
    }

    @Bean
    AllowedCodesConfig getAllowedCodesConfig() {
        return this;
    }
}
