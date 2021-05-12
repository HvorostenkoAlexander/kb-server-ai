package com.nlmk.kb.server.entity;

import com.nlmk.kb.server.entity.pdm.PdmDictionary;
import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * сообщение кафка PDM-справочники
 */
@Data
@Entity
@Table(name = "pdm_message")
@TypeDef(name = "json", typeClass = JsonType.class)
public class PdmMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "id")
    private Long id;

    @Column(nullable = false)
    private String topic;

    @Column(nullable = false)
    private Integer partition;

    @Column(name = "msg_offset",
            nullable = false)
    private Long offset;

    @Column(name = "msg_key",
            nullable = false)
    private String key;

    @Column(nullable = false)
    private String ts;

    @Column(nullable = false)
    private String op;

    @Type(type = "json")
    @Column(columnDefinition = "json")
    private PdmDictionary dictionary;
}
