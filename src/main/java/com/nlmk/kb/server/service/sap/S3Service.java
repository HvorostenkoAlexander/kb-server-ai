package com.nlmk.kb.server.service.sap;

import com.nlmk.attestation.zmmorder.ZMMORDERS05DOP;
import com.nlmk.attestation.zorder.ZORDERS051;

/**
 * Сервис для работы с хранилищем S3
 */
public interface S3Service {

    /**
     * Получение файла из SAP
     *
     * @param bucket Местоположение файла xml с данными по заказу (корзина S3 хранилища)
     * @param path   Имя файла, содержащего xml с данными по заказу
     * @return строчное представление файла
     */
    String getObjectAsStringFromBucket(String bucket, String path);

    /**
     * Десериализация заказа ZORDERS051
     *
     * @param zorder строчное представление файла в виде xml с данными по заказу
     * @return заказ ZORDERS051
     */
    ZORDERS051 unmarshalZorder(String zorder);

    /**
     * Десериализация заказа ZMMORDERS05DOP
     *
     * @param zmmorder строчное представление файла в виде xml с данными по заказу
     * @return заказ ZMMORDERS05DOP
     */
    ZMMORDERS05DOP unmarshalZmmorder(String zmmorder);
}
