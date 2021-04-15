package com.nlmk.kb.server;

import com.nlmk.kb.server.config.SupConsumerProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(value = {SupConsumerProperties.class})
public class KbServerApplication {

    public static void main(String[] args) {

        SpringApplication.run(KbServerApplication.class, args);
    }

}
