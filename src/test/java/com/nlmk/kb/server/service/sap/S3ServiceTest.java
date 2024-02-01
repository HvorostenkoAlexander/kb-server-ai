package com.nlmk.kb.server.service.sap;

import com.nlmk.attestation.product.api.order.SapName;
import com.nlmk.attestation.zmmorder.ZMMORDERS05DOP;
import com.nlmk.attestation.zorder.ZORDERS051;
import com.nlmk.kb.server.exception.S3ClientException;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;

import java.util.Arrays;

import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;


import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
@SpringBootTest
class S3ServiceTest {

    @Autowired
    private S3Service s3Service;

    @MockBean
    @Qualifier("idoczordrsS3Client")
    private MinioClient idoczordrsS3Client;

    @MockBean
    @Qualifier("zmmordersdopS3Client")
    private MinioClient zmmordersdopS3Client;

    private static final String IDOCZORDRS_BUCKET_NAME = "idoczordrs";
    private static final String ZMMORDERSDOP_BUCKET_NAME = "zmmordersdop";

    @ParameterizedTest
    @ValueSource(strings = {IDOCZORDRS_BUCKET_NAME, ZMMORDERSDOP_BUCKET_NAME})
    void canGetValidXmlOrderFromS3(String bucketName) throws Exception {
        // given
        final String path = "zordersExample.xml";
        InputStream mockStream = new FileInputStream("src/test/resources/xml/" + path);
        when(idoczordrsS3Client.getObject(any()))
                .thenReturn(new GetObjectResponse(null, bucketName, null, path, mockStream));
        when(zmmordersdopS3Client.getObject(any()))
                .thenReturn(new GetObjectResponse(null, bucketName, null, path, mockStream));

        // when
        String xml = s3Service.getObjectAsStringFromBucket(bucketName, path);
        ZORDERS051 result = s3Service.unmarshalZorder(xml);

        // then
        Assertions.assertNotNull(result);
        Assertions.assertEquals("0040452892", result.getIDOC().getE1EDK01().getBELNR());

        if (bucketName.equals(IDOCZORDRS_BUCKET_NAME)) {
            verify(idoczordrsS3Client, times(1)).getObject(any());
            verify(zmmordersdopS3Client, times(0)).getObject(any());
        } else {
            verify(idoczordrsS3Client, times(0)).getObject(any());
            verify(zmmordersdopS3Client, times(1)).getObject(any());
        }
    }

    @Test
    void canReplaceColumnToPointFOrKoefRasteskXmlOrderFromS3() throws Exception {
        final String bucketName = IDOCZORDRS_BUCKET_NAME;
        // given
        final String path = "zorderKoefRastresk.xml";
        InputStream mockStream = new FileInputStream("src/test/resources/xml/" + path);
        when(idoczordrsS3Client.getObject(any()))
                .thenReturn(new GetObjectResponse(null, bucketName, null, path, mockStream));

        // when
        String xml = s3Service.getObjectAsStringFromBucket(bucketName, path);
        ZORDERS051 result = s3Service.unmarshalZorder(xml);
        var resultValue = result.getIDOC().getE1CUCFG().stream().flatMap(e1CUCFG -> {
            return e1CUCFG.getE1CUVAL().stream()
                    .filter(cuval -> cuval.getCHARC().equals(SapName.KOEF_RASTRESK_MAX.getTag()))
                    .collect(Collectors.toList()).stream();
        }).findFirst().map(it -> it.getVALUE()).orElse(null);

        // then
        Assertions.assertNotNull(result);
        Assertions.assertEquals("0.24", resultValue);

        verify(idoczordrsS3Client, times(1)).getObject(any());
    }

    @Test
    void getObjectAsStringShouldThrowS3ClientExceptionIfProvidedUnknownBucketName() throws Exception {
        // given
        final String unknownBucketName = "bucket";
        final String path = "zordersExample.xml";
        // when
        // then
        assertThatThrownBy(() -> s3Service.getObjectAsStringFromBucket(unknownBucketName, path))
                .isInstanceOf(S3ClientException.class)
                .hasMessageContaining("Encountered unknown bucket name: [bucket]. Known bucket names are [idoczordrs, zmmordersdop]");
        verify(zmmordersdopS3Client, times(0)).getObject(any());
        verify(idoczordrsS3Client, times(0)).getObject(any());
    }

    @Test
    void getZorderShouldThrowS3ClientExceptionIfProvidedInvalidXml() {
        // given
        // when
        // then
        assertThatThrownBy(() -> s3Service.unmarshalZorder("Invalid xml"))
                .isInstanceOf(S3ClientException.class)
                .hasMessageContaining("Order cannot be unmarshalled as ZORDER.");
    }

    @Test
    void zorderGarbageCleaningTest() throws IOException {

        final String xmlZorder = new String(Files.readAllBytes(Path.of("src/test/resources/xml/zordersTestVariant.xml")));

        ZORDERS051 zorder = s3Service.unmarshalZorder(xmlZorder);

        zorder.getIDOC().getE1CUCFG().forEach(
                e1cucfg -> e1cucfg.getE1CUVAL().forEach(e1cuval -> {
                    // все коды, которые есть в заказе
                    SapName sapName = Arrays.stream(SapName.values())
                            .filter(a -> a.getTag().equals(e1cuval.getCHARC())).findFirst().orElseThrow();
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
                }));
    }

    @Test
    void zmmorderGarbageCleaningTest() throws IOException {
        final String xmlZmmorder = new String(Files.readAllBytes(Path.of("src/test/resources/xml/zmmordersTestVariant.xml")));
        ZMMORDERS05DOP zmmorders = s3Service.unmarshalZmmorder(xmlZmmorder);
        zmmorders.getIDOC().getE1CUCFG().forEach(e1CUCFG ->
                e1CUCFG.getE1CUVAL().forEach(e1cuval -> {
                    // все коды, которые есть в заказе
                    SapName sapName = Arrays.stream(SapName.values())
                            .filter(a -> a.getTag().equals(e1cuval.getCHARC())).findFirst().orElseThrow();
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
                }));
    }
}
