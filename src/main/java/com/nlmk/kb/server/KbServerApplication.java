package com.nlmk.kb.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
public class KbServerApplication {

    public static void main(String[] args) {
         SpringApplication.run(KbServerApplication.class, args);

//        SpringApplication application = new SpringApplication(KbServerApplication.class);
//        ConfigurableEnvironment environment = new StandardEnvironment();
//        environment.setActiveProfiles("prod");
//        application.setEnvironment(environment);
//
//        application.run(args);
    }

}
