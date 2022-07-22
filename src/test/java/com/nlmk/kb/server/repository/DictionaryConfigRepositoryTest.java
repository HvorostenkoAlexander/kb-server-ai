package com.nlmk.kb.server.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

@DataJpaTest
class DictionaryConfigRepositoryTest {

    @Autowired
    private DictionaryConfigRepository repository;

    @Test
    @Sql(scripts = "classpath:/sql/dictionary_config.sql")
    void findByTopic() {
        Assertions.assertEquals(2L, repository.count());

        Assertions.assertTrue(repository.findByTopic("t1").isEmpty());

        final var topic1 = repository.findByTopic("topic.pdm.dict-1.0");
        Assertions.assertTrue(topic1.isPresent());
        Assertions.assertTrue(topic1.get().getEnabled());
        Assertions.assertEquals(List.of(2,3,138,333), topic1.get().getCodes());

        final var topic2 = repository.findByTopic("topic.pdm.dict-2.0");
        Assertions.assertTrue(topic2.isPresent());
        Assertions.assertFalse(topic2.get().getEnabled());
        Assertions.assertEquals(List.of(1,3), topic2.get().getCodes());
    }

}
