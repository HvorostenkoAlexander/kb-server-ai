package com.nlmk.kb.server.service.sap;

import com.nlmk.attestation.zmmorder.ZMMORDERS05DOP;
import com.nlmk.attestation.product.api.order.SapName;
import com.nlmk.attestation.zorder.ZORDERS051;
import com.nlmk.kb.server.exception.S3ClientException;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Objects;

@Slf4j
@Component
public class S3ServiceImpl implements S3Service {

    private final Unmarshaller zorderUnmarshaller;
    private final Unmarshaller zmmorderUnmarshaller;
    private final MinioClient idoczordrsS3Client;
    private final MinioClient zmmordersdopS3Client;

    private final String idoczordrsBucketName;
    private final String zmmordersdopBucketName;

    private static final String EMPTY_VALUE_TEMPLATE = "-";
    private static final String COMMA_VALUE_TEMPLATE = "^(\\d+,){1,5}\\d+\\.*\\d+$";

    public S3ServiceImpl(@Qualifier("idoczordrsS3Client") MinioClient idoczordrsS3Client,
                         @Qualifier("zmmordersdopS3Client") MinioClient zmmordersdopS3Client,
                         @Value("${s3.idoczordrs.bucket-name}") String idoczordrsBucketName,
                         @Value("${s3.zmmordersdop.bucket-name}") String zmmordersdopBucketName) throws JAXBException {
        this.idoczordrsS3Client = idoczordrsS3Client;
        this.zmmordersdopS3Client = zmmordersdopS3Client;
        this.idoczordrsBucketName = idoczordrsBucketName;
        this.zmmordersdopBucketName = zmmordersdopBucketName;
        this.zorderUnmarshaller = JAXBContext.newInstance(ZORDERS051.class).createUnmarshaller();
        this.zmmorderUnmarshaller = JAXBContext.newInstance(ZMMORDERS05DOP.class).createUnmarshaller();
    }

    @Override
    public String getObjectAsStringFromBucket(String bucket, String path) {
        try {
            GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(path)
                    .build();
            if (bucket.equals(idoczordrsBucketName)) {
                return new String(idoczordrsS3Client.getObject(getObjectArgs).readAllBytes());
            } else if (bucket.equals(zmmordersdopBucketName)) {
                return new String(zmmordersdopS3Client.getObject(getObjectArgs).readAllBytes());
            } else {
                final String msg = String.format("Encountered unknown bucket name: [%s]. Known bucket names are [%s, %s]",
                        bucket, idoczordrsBucketName, zmmordersdopBucketName);
                log.error("getObjectAsString. {}}", msg);
                throw new S3ClientException(msg);
            }
        } catch (Exception ex) {
            final String msg = String.format("Error getting object. bucket: [%s], path: [%s]. Exception : [%s]", bucket, path, ex.getMessage());
            log.error("getObjectAsString. {}", msg);
            throw new S3ClientException(msg);
        }
    }

    @Override
    public ZORDERS051 unmarshalZorder(String zorder) {
        try {
            var zorders051 = (ZORDERS051) zorderUnmarshaller.unmarshal(new StringReader(zorder));
            cleanGarbage(zorders051);
            return zorders051;
        } catch (JAXBException e) {
            final String msg = String.format("Order cannot be unmarshalled as ZORDER. order: [%s]. Exceptiton: [%s]", zorder, e.getMessage());
            log.error("unmarshalZorder. {}", msg);
            throw new S3ClientException(msg);
        }
    }

    @Override
    public ZMMORDERS05DOP unmarshalZmmorder(String zmmorder) {
        try {
            var zmmorders = (ZMMORDERS05DOP) zmmorderUnmarshaller.unmarshal(new StringReader(zmmorder));
            cleanGarbage(zmmorders);
            return zmmorders;
        } catch (JAXBException e) {
            final String msg = String.format("Order cannot be unmarshalled as ZMMORDERS05DOP. order: [%s]. Exceptiton: [%s]", zmmorder, e.getMessage());
            log.error("unmarshalZmmorder. {}", msg);
            throw new S3ClientException(msg);
        }
    }

    /**
     * Чистка поля value от мусора, выборочно
     */
    private void cleanGarbage(ZORDERS051 zorder) {
        if (Objects.isNull(zorder)
                || Objects.isNull(zorder.getIDOC())
                || CollectionUtils.isEmpty(zorder.getIDOC().getE1CUCFG())) {
            return;
        }

        zorder.getIDOC().getE1CUCFG().forEach(
                e1cucfg ->
                        // одна позиция заказа
                        e1cucfg.getE1CUVAL().forEach(
                                // один код признака (только знакомые коды)
                                e1cuval -> Arrays.stream(SapName.values())
                                        .filter(value -> value.getTag().equals(e1cuval.getCHARC()))
                                        .findFirst()
                                        .ifPresent(code -> e1cuval.setVALUE(trimValue(e1cuval.getVALUE(), code)))
                        ));
    }

    private void cleanGarbage(ZMMORDERS05DOP zmmorder) {
        if (Objects.isNull(zmmorder)
                || Objects.isNull(zmmorder.getIDOC())
                || CollectionUtils.isEmpty(zmmorder.getIDOC().getE1CUCFG())) {
            return;
        }

        zmmorder.getIDOC().getE1CUCFG().forEach(
                e1cucfg ->
                        // одна позиция заказа
                        e1cucfg.getE1CUVAL().forEach(
                                // один код признака (только знакомые коды)
                                e1cuval -> Arrays.stream(SapName.values())
                                        .filter(value -> value.getTag().equals(e1cuval.getCHARC()))
                                        .findFirst()
                                        .ifPresent(code -> e1cuval.setVALUE(trimValue(e1cuval.getVALUE(), code)))
                        ));
    }

    private String trimValue(String value, SapName code) {
        if (StringUtils.isNotBlank(value)) {
            if (value.trim().equals(EMPTY_VALUE_TEMPLATE)) {
                return null;
            }
            // Коэффициент растрескивания КЦ2 ОПЭ-42 [3] Коэффициент трещиностойкости требования из заказа
            // Руководитель команды SAP указал, что не могут заменить запятую на точку
            // Поэтому первая правая запятая будет заменена на точку
            if (code.equals(SapName.KOEF_RASTRESK_MIN) || code.equals(SapName.KOEF_RASTRESK_MAX)) {
                var commaIndex = value.lastIndexOf(',');
                if (commaIndex >= 0) {
                    char[] chars = value.toCharArray();
                    chars[commaIndex] = '.';
                    var updatedValue = String.valueOf(chars);
                    //Если есть еще запятые
                    if (updatedValue.trim().matches(COMMA_VALUE_TEMPLATE)) { // 1,234,567.8 => 1234567.8
                        return updatedValue.trim().replace(",", "");
                    } else {
                        return updatedValue;
                    }
                }
            }
            if (value.trim().matches(COMMA_VALUE_TEMPLATE)) { // 1,234,567.8 => 1234567.8
                return value.trim().replace(",", "");
            }
        }
        // без изменений
        return value;
    }

}
