package com.nlmk.kb.server.service.sap;

import com.nlmk.attestation.zorder.ZORDERS051;
import com.nlmk.kb.server.exception.S3ClientException;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;


import static org.mockito.ArgumentMatchers.any;

@Slf4j
@SpringBootTest
class S3ServiceTest {

    @Autowired
    private S3Service s3Service;

    @MockBean
    private MinioClient s3Client;

    @Test
    void zorderUnmarshalling() throws Exception {
        final String path = "zordersExample.xml";
        final String bucket = "bucket";

        Mockito.when(s3Client.getObject(any()))
                .thenReturn(new GetObjectResponse(null, bucket, null, path, new ByteArrayInputStream("incorrect xml".getBytes())));

        String xml1 = s3Service.getObjectAsString(bucket, path);

        Assertions.assertEquals("incorrect xml", xml1);

        Assertions.assertThrows(S3ClientException.class, () -> s3Service.getZorder(xml1));


        InputStream mockStream = new FileInputStream(new File("src/test/resources/xml/" + path));

        Mockito.when(s3Client.getObject(any()))
                .thenReturn(new GetObjectResponse(null, bucket, null, path, mockStream));

        String xml2 = s3Service.getObjectAsString(bucket, path);
        ZORDERS051 result = s3Service.getZorder(xml2);

        Assertions.assertNotNull(result);

        Assertions.assertEquals("0040452892", result.getIDOC().getE1EDK01().getBELNR());
    }

}
