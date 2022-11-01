package com.nlmk.kb.server.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Getter
public class AllowedCodesConfig {

    private String allowedAnalysisCodes;

    private String allowedMechanicalCodes;

    public AllowedCodesConfig(@Value("${apcs-allowed.analysis.codes}") String allowedAnalysisCodes,
                              @Value("${apcs-allowed.mechanical.codes}") String allowedMechanicalCodes) {
        this.allowedAnalysisCodes = allowedAnalysisCodes;
        this.allowedMechanicalCodes = allowedMechanicalCodes;
    }

    @Bean
    AllowedCodesConfig getAllowedCodesConfig() {
        return this;
    }
}
