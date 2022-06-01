package com.nlmk.kb.server.service;

import com.nlmk.attestation.zorder.ZORDERS051;

/**
 * Сервис для работы с хранилищем S3
 */
public interface S3Service {

    /**
     * Получение файла из SAP
     *
     * @param bucket Местоположение файла xml с данными по заказу (корзина S3 хранилища)
     * @param path Имя файла, содержащего xml с данными по заказу
     * @return строчное представление файла
     */
    String getObjectAsString(String bucket, String path);

    /**
     * Десериализация заказа
     *
     * @param order строчное представление файла в виде xml с данными по заказу
     * @return заказ
     */
    ZORDERS051 getZorder(String order);

}
