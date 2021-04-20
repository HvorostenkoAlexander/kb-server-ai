package com.nlmk.kb.server.entity;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;

@Data
@MappedSuperclass
public class BaseParam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "id")
    private Long id;

    @Column(nullable = false)
    private String ts;//дата и время передачи

    @Column(nullable = false)
    private String operation;// операция

    @Column(nullable = false)
    private Integer recordPk;//Id EM СУП

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    @JoinColumn(unique = false)
    private RecData data;// может быть null
}
