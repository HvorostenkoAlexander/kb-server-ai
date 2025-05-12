package com.nlmk.kb.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

@EnableKafka
@SpringBootApplication
@Slf4j
public class KbServerApplication {

    private static final int ONE_MB_BYTES = 1024 * 1024;

    public static void main(String[] args) {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        long xmx = memoryBean.getHeapMemoryUsage().getMax() / ONE_MB_BYTES;
        long xms = memoryBean.getHeapMemoryUsage().getInit() / ONE_MB_BYTES;
        log.info("Initial Memory (xms) : [{}]mb", xms);
        log.info("Max Memory (xmx) : [{}]mb", xmx);
        SpringApplication.run(KbServerApplication.class);
    }

}
