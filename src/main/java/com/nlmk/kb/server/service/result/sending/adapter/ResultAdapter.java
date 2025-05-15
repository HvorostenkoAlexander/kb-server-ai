package com.nlmk.kb.server.service.result.sending.adapter;

import com.nlmk.attestation.product.api.ProductDto;
import nlmk.l3.apcs.RecordPk;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.util.Assert;

/**
 * Адаптация Единицы Продукции к формату передачи результата Аттестации
 */
public interface ResultAdapter<T extends SpecificRecordBase> {

    T adapt(ProductDto product, boolean isNew);

    /**
     * Наименования головного объекта Avro-схемы
     */
    String getAvroName();

    /**
     * С каким типом данных работает адаптер (Class)
     */
    Class<T> getSendingType();

    /**
     * Получение первичного ключа
     */
    default RecordPk getPk(SpecificRecordBase recordBase) {
        return null;
    }

    /**
     * Получение первичного ключа системы MES
     */
    default nlmk.apcs.verification.results.cgp.v0.RecordPk getMesPk(SpecificRecordBase recordBase) {
        return null;
    }

    default void checkProduct(ProductDto product) {
        Assert.notNull(product, "product не может быть null");
        Assert.notEmpty(product.getRequests(), "product.getRequests() не может быть пустым.");
        Assert.notEmpty(product.getRequests().get(0).getAttestations(), "product.getRequests().get(0).getAttestations() не может быть пустым.");
    }
}
