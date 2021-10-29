package com.nlmk.kb.server.config;

import io.jaegertracing.internal.JaegerTracer;
import io.jaegertracing.internal.samplers.ConstSampler;
import io.opentracing.util.GlobalTracer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class JaegerConfig {

    private final String serviceName;
    private final String udpSenderHost;
    private final int udpSenderPort;
    private final boolean logSpanEnabled;

    public JaegerConfig(@Value("${spring.application.name}")
                                String serviceName,
                        @Value("${apcs-jaeger.udp-sender.host}")
                                String udpSenderHost,
                        @Value("${apcs-jaeger.udp-sender.port}")
                                int udpSenderPort,
                        @Value("${apcs-jaeger.log-span.enabled}")
                                boolean logSpanEnabled) {
        this.serviceName = serviceName;
        this.udpSenderHost = udpSenderHost;
        this.udpSenderPort = udpSenderPort;
        this.logSpanEnabled = logSpanEnabled;
    }

    @Bean
    public JaegerTracer jaegerTracer() {

        return new io.jaegertracing.Configuration(serviceName)
                .withSampler(new io.jaegertracing.Configuration.SamplerConfiguration()
                        .withType(ConstSampler.TYPE)
                        .withParam(1))
                .withReporter(new io.jaegertracing.Configuration.ReporterConfiguration()
                        .withSender(
                                new io.jaegertracing.Configuration.SenderConfiguration()
                                        .withAgentHost(udpSenderHost)
                                        .withAgentPort(udpSenderPort)
                        )
                        .withLogSpans(logSpanEnabled)
                ).getTracer();
    }

    @PostConstruct
    public void registerToGlobalTracer() {
        if (!GlobalTracer.isRegistered()) {
            GlobalTracer.registerIfAbsent(jaegerTracer());
        }
    }
}
