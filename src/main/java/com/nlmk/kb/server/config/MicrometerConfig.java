package com.nlmk.kb.server.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class MicrometerConfig {

    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {

        log.info("--- timedAspect: MeterRegistry.config: [{}]", registry.config());

        return new TimedAspect(registry);
    }
}
