package com.nlmk.kb.server.service.sap;

import com.nlmk.attestation.product.api.order.SapName;
import com.nlmk.attestation.zorder.ZORDERS051;
import com.nlmk.kb.server.exception.S3ClientException;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;


import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
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


        InputStream mockStream = new FileInputStream("src/test/resources/xml/" + path);

        Mockito.when(s3Client.getObject(any()))
                .thenReturn(new GetObjectResponse(null, bucket, null, path, mockStream));

        String xml2 = s3Service.getObjectAsString(bucket, path);
        ZORDERS051 result = s3Service.getZorder(xml2);

        Assertions.assertNotNull(result);

        Assertions.assertEquals("0040452892", result.getIDOC().getE1EDK01().getBELNR());
    }

    @Test
    void garbageCleaningTest() throws IOException {

        final String xmlZOrder = new String(Files.readAllBytes(Path.of("src/test/resources/xml/zordersTestVariant.xml")));

        ZORDERS051 zorder = s3Service.getZorder(xmlZOrder);

        zorder.getIDOC().getE1CUCFG().forEach(
                e1cucfg -> e1cucfg.getE1CUVAL().forEach(
                        e1cuval -> {
                            // все коды, которые есть в заказе
                            SapName sapName = Arrays.stream(SapName.values())
                                    .filter(a -> a.getTag().equals(e1cuval.getCHARC())).findFirst().get();
                            switch (sapName) {
                                case STNDRT_PROD:
                                case STNDRT_MARKA:
                                case STNDRT_SORT:
                                    assertEquals("ГОСТ 14918-2020", e1cuval.getVALUE());
                                    break;
                                case CEH_PROD:
                                    assertEquals("11", e1cuval.getVALUE());
                                    break;
                                case MARKA:
                                    assertEquals("02", e1cuval.getVALUE());
                                    break;
                                case VID_POSTAVKI:
                                    assertEquals("РЛН", e1cuval.getVALUE());
                                    break;
                                case KROM:
                                    assertEquals("НО", e1cuval.getVALUE());
                                    break;
                                case TPRK:
                                    assertEquals("ТУ 0027", e1cuval.getVALUE());
                                    break;
                                case TEXK:
                                case RABPL:
                                case GROT:
                                case ROUTE_TK:
                                    assertNull(e1cuval.getVALUE(),
                                            MessageFormat.format("not NULL value in sapName {0}", sapName));
                                    break;
                                case SHOT_MIN:
                                    assertEquals("1250.0", e1cuval.getVALUE());
                                    break;
                                case SHOT_MAX:
                                    assertEquals("1234567.8", e1cuval.getVALUE());
                                    break;
                                case DLIN_MIN:
                                    assertEquals("6000", e1cuval.getVALUE());
                                    break;
                                case DLIN_MAX:
                                    assertEquals("3000.0", e1cuval.getVALUE());
                                    break;
                                case VN_DIAM_RL:
                                    assertEquals("600", e1cuval.getVALUE());
                                    break;
                            }
                        }
                )
        );
    }
}
