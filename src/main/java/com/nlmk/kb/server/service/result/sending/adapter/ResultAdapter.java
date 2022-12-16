package com.nlmk.kb.server.service.result.sending.adapter;

import com.nlmk.attestation.product.api.ProductDto;
import nlmk.l3.apcs.RecordPk;
import org.apache.avro.specific.SpecificRecordBase;

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
    RecordPk getPk(SpecificRecordBase recordBase);

}
