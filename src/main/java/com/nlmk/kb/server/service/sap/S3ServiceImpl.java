package com.nlmk.kb.server.service.sap;

import com.nlmk.attestation.zorder.ZORDERS051;
import com.nlmk.kb.server.exception.S3ClientException;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

@Slf4j
@Component
public class S3ServiceImpl implements S3Service {

    private final Unmarshaller jaxbUnmarshaller;
    private final MinioClient s3Client;

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
            return (ZORDERS051) jaxbUnmarshaller.unmarshal(new StringReader(order));
        } catch (JAXBException e) {
            final String msg = String.format("Order cannot be unmarshalled as ZORDER. order: [%s]. Exceptiton: [%s]", order, e.getMessage());
            log.error("getZorder. {}", msg);
            throw new S3ClientException(msg);
        }
    }

}
