package com.nlmk.kb.server.aspects;

import com.nlmk.kb.server.config.KbConstants;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Aspect
@Component
public class KafkaListenerAspect {

    @Around("@annotation(org.springframework.kafka.annotation.KafkaListener))")
    public void wrapKafkaListener(ProceedingJoinPoint joinPoint) throws Throwable {

        MDC.put(KbConstants.KAFKA_ID, KbConstants.KAFKA_PREFIX + UUID.randomUUID());

        try {
            if (joinPoint != null) {
                joinPoint.proceed(joinPoint.getArgs());
            }
        } finally {
            MDC.remove(KbConstants.KAFKA_ID);
        }
    }

}
