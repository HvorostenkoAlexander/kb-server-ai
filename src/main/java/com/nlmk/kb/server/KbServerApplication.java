package com.nlmk.kb.server;

import com.nlmk.kb.server.config.SupConsumerProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
@EnableConfigurationProperties(value = {SupConsumerProperties.class})
public class KbServerApplication {

    public static void main(String[] args) {
         //SpringApplication.run(KbServerApplication.class, args);

        SpringApplication application = new SpringApplication(KbServerApplication.class);
        ConfigurableEnvironment environment = new StandardEnvironment();
        environment.setActiveProfiles("prod");
        application.setEnvironment(environment);

        application.run(args);
    }

}
