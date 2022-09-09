package com.nlmk.kb.server.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "result_config")
public class ResultsConfig {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String topic;
    @Column(nullable = false)
    private String avroName; // Наименование головного объекта Avro-схемы
    private String condition;
    @Column(nullable = false)
    private boolean enabled;

}
