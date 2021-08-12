package com.nlmk.kb.server.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "record_specifications")
public class RecSpec {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "id")
    private Long id;

    @Column(nullable = false)
    private Integer specCode;//код характеристики

    @Column(nullable = false)
    private String specName;//наименование характеристики

    @Column(nullable = false)
    private Integer specTypeCode;// тип данных (1-string, 2-number, 3-date)

    @Column(nullable = false)
    private String specTypeName;// нименование типа данных

    @Column(nullable = true)
    private Float specValue;// значение характеристики

    @Column(nullable = true)
    private String specFormat;// формат передачи характеристики

    @Column(nullable = true)
    private String specMeasure;// еденица измерения
}
