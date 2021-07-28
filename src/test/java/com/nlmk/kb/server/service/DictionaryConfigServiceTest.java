package com.nlmk.kb.server.service;

import com.nlmk.kb.server.entity.configurator.DictionaryConfig;
import com.nlmk.kb.server.repository.DictionaryConfigRepository;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import java.util.List;

@SpringBootTest
public class DictionaryConfigServiceTest {

    @Autowired
    private DictionaryConfigService service;

    @Autowired
    private DictionaryConfigRepository repository;

    private DictionaryConfig validEntity;

    @BeforeEach
    void setUp(){
        validEntity = DictionaryConfig.builder()

                .topic("topic")
                .nsiPath("/dict/path")
                .codes(List.of(1,2,3,4,5))
                .enabled(true)
                .build();
        repository.save(validEntity);
    }

    @Test
    void findDtoByIdOk(){
//        val dto = service.findById(validEntity.getId());
//        System.out.println("---dto: "+dto);
        System.out.println("list: "+repository.findAll());
    }
}
