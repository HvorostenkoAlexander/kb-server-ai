package com.nlmk.kb.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

@SpringBootApplication
public class KbServerApplication {

    public static void main(String[] args) throws IOException {

        FileCopyUtils.copy(new ClassPathResource("ssl/client.truststore.jks").getInputStream(),
                new FileOutputStream("kafkaSsl/client.truststore.jks"));

        FileCopyUtils.copy(new ClassPathResource("ssl/client.keystore.jks").getInputStream(),
                new FileOutputStream("kafkaSsl/client.keystore.jks"));

        SpringApplication.run(KbServerApplication.class, args);
    }

}
