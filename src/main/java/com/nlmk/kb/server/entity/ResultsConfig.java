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
    @Column(nullable = false, name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "topic")
    private String topic;

    @Column(nullable = false, name = "avro")
    private String avroName;

    private String condition;

    @Column(nullable = false, name = "is_enabled")
    private boolean enabled;

}
