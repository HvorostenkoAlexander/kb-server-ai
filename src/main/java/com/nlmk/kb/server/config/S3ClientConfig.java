package com.nlmk.kb.server.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3ClientConfig {

    private final String s3Url;
    private final String idoczordrsAccessKey;
    private final String idoczordrsSecretKey;
    private final String zmmordersdopAccessKey;
    private final String zmmordersdopSecretKey;

    public S3ClientConfig(
            @Value("${s3.url}") String s3Url,
            @Value("${s3.idoczordrs.access-key}") String idoczordrsAccessKey,
            @Value("${s3.idoczordrs.secret-key}") String idoczordrsSecretKey,
            @Value("${s3.zmmordersdop.access-key}") String zmmordersdopAccessKey,
            @Value("${s3.zmmordersdop.secret-key}") String zmmordersdopSecretKey) {
        this.s3Url = s3Url;
        this.idoczordrsAccessKey = idoczordrsAccessKey;
        this.idoczordrsSecretKey = idoczordrsSecretKey;
        this.zmmordersdopAccessKey = zmmordersdopAccessKey;
        this.zmmordersdopSecretKey = zmmordersdopSecretKey;
    }

    @Bean("idoczordrsS3Client")
    public MinioClient idoczordrsS3Client() {
        return MinioClient.builder()
                .endpoint(s3Url)
                .credentials(idoczordrsAccessKey, idoczordrsSecretKey)
                .build();
    }

    @Bean("zmmordersdopS3Client")
    public MinioClient zmmordersdopS3Client() {
        return MinioClient.builder()
                .endpoint(s3Url)
                .credentials(zmmordersdopAccessKey, zmmordersdopSecretKey)
                .build();
    }
}
