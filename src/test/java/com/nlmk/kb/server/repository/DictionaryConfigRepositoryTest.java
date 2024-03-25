package com.nlmk.kb.server.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DictionaryConfigRepositoryTest {

    @Autowired
    private DictionaryConfigRepository repository;

    @Test
    @Sql(scripts = "classpath:/sql/dictionary_config.sql")
    void findByTopic() {
        assertThat(repository.count()).isEqualTo(2L);

        assertThat(repository.findByTopic("t1")).isEmpty();

        final var topic1 = repository.findByTopic("topic.pdm.dict-1.0");
        assertThat(topic1).hasValueSatisfying(topic -> {
            assertThat(topic.getEnabled()).isTrue();
            assertThat(topic.getCodes()).containsExactly(2, 3, 138, 333);
        });

        final var topic2 = repository.findByTopic("topic.pdm.dict-2.0");
        assertThat(topic2).hasValueSatisfying(topic -> {
            assertThat(topic.getEnabled()).isFalse();
            assertThat(topic.getCodes()).containsExactly(1, 3);
        });
    }

}
