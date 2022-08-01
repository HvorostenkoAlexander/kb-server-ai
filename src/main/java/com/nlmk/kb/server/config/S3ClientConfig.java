package com.nlmk.kb.server.config;

import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class S3ClientConfig {

    private final String s3Url;
    private final String accessKey;
    private final String secretKey;

    public S3ClientConfig(
            @Value("${s3.url}") String s3Url,
            @Value("${s3.accessKey}") String accessKey,
            @Value("${s3.secretKey}") String secretKey)
    {
        this.s3Url = s3Url;
        this.accessKey = accessKey;
        this.secretKey =secretKey;
    }

    @Bean
    public MinioClient s3Client() {
        return MinioClient.builder()
                .endpoint(s3Url)
                .credentials(accessKey, secretKey)
                .build();
    }
}
