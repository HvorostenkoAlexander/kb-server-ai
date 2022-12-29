package com.nlmk.kb.server.service.sap;

import com.nlmk.attestation.product.api.order.SapName;
import com.nlmk.attestation.zorder.ZORDERS051;
import com.nlmk.kb.server.exception.S3ClientException;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

    private final Unmarshaller jaxbUnmarshaller;
    private final MinioClient s3Client;

    private static final String EMPTY_VALUE_TEMPLATE = "-";
    private static final String COMMA_VALUE_TEMPLATE = "^(\\d+,){1,5}\\d+\\.*\\d+$";

    public S3ServiceImpl(MinioClient s3Client) throws JAXBException {
        this.s3Client = s3Client;
        this.jaxbUnmarshaller = JAXBContext.newInstance(ZORDERS051.class).createUnmarshaller();
    }

    @Override
    public String getObjectAsString(String bucket, String path) {
        try {
            return new String(s3Client.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(path)
                            .build()).readAllBytes());
        } catch (Exception ex) {
            final String msg = String.format("Error getting object. bucket: [%s], path: [%s]. Exception : [%s]", bucket, path, ex.getMessage());
            log.error("getObjectAsString. {}", msg);
            throw new S3ClientException(msg);
        }
    }

    @Override
    public ZORDERS051 getZorder(String order) {
        try {
            var zorder = (ZORDERS051) jaxbUnmarshaller.unmarshal(new StringReader(order));
            garbageCleaning(zorder);
            return zorder;
        } catch (JAXBException e) {
            final String msg = String.format("Order cannot be unmarshalled as ZORDER. order: [%s]. Exceptiton: [%s]", order, e.getMessage());
            log.error("getZorder. {}", msg);
            throw new S3ClientException(msg);
        }
    }

    /**
     * Чистка поля value от мусора, выборочно
     */
    private void garbageCleaning(ZORDERS051 zorder) {
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
                                        .filter(name -> name.toString().equals(e1cuval.getCHARC()))
                                        .findFirst()
                                        .ifPresent(code -> e1cuval.setVALUE(trimValue(e1cuval.getVALUE())))
                        ));
    }

    private String trimValue(String value) {
        if (StringUtils.isNotBlank(value)) {
            if (value.trim().equals(EMPTY_VALUE_TEMPLATE)) {
                return null;
            }
            if (value.trim().matches(COMMA_VALUE_TEMPLATE)) { // 1,234,567.8 => 1234567.8
                return value.trim().replace(",", "");
            }
        }
        // без изменений
        return value;
    }

}
