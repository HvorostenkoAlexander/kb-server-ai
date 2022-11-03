package com.nlmk.kb.server.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "dictionary_config")
public class DictionaryConfig {

    @Id
    @Column(nullable = false, name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, name = "topic")
    private String topic; // имя топика

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "dictionary_config_codes", joinColumns = @JoinColumn(name = "dictionary_config_id"))
    private List<Integer> codes; // перечень характеристик(кодов)

    @Column(nullable = false, name = "nsi_path")
    private String nsiPath; // точка куда скидывать данные

    @Column(nullable = false, name = "is_enabled")
    private Boolean enabled; // активность

}
