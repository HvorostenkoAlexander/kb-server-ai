package com.nlmk.kb.server.service.pdm;

import com.nlmk.kb.server.api.DictionaryConfigDto;
import com.nlmk.kb.server.entity.DictionaryConfig;
import com.nlmk.kb.server.repository.DictionaryConfigRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class DictionaryConfigServiceTest {

    @Autowired
    private DictionaryConfigService service;

    @Autowired
    private DictionaryConfigRepository repository;

    private DictionaryConfig validEntity;

    @BeforeEach
    void setUp() {
        validEntity = DictionaryConfig.builder()
                .topic(RandomStringUtils.randomAlphabetic(12))
                .nsiPath("/dict/path")
                .codes(List.of(101, 202, 203))
                .enabled(true)
                .build();
        repository.save(validEntity);
    }

    @AfterEach
    void tearDown() {
        repository.deleteById(validEntity.getId());
    }

    @Test
    void findDtoByIdTestOk() {
        // given
        final var dto = service.findById(validEntity.getId());

        // when
        // then
        assertThat(dto.getId()).isEqualTo(validEntity.getId());
        assertThat(dto.getTopic()).isEqualTo(validEntity.getTopic());
        assertThat(dto.getNsiPath()).isEqualTo(validEntity.getNsiPath());
        assertThat(dto.getEnabled()).isEqualTo(validEntity.getEnabled());
        assertThat(dto.getCodes()).isEqualTo(validEntity.getCodes().toArray());
    }

    @Test
    void findByIdTestNotFound() {
        assertThatThrownBy(() -> service.findById(Long.MAX_VALUE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Не найден объект с id: [" + Long.MAX_VALUE + "]");
    }

    @Test
    void updateTestOk() {
        // given
        final var dto = DictionaryConfigDto.builder()
                .topic("updatedTopic")
                .nsiPath("/updated/updatedPath")
                .enabled(false)
                .codes(new Integer[]{101, 202, 303, 404})
                .build();
        final var actualDto = service.update(validEntity.getId(), dto);

        // when
        // then
        assertThat(actualDto.getId()).isEqualTo(validEntity.getId());
        assertThat(actualDto.getTopic()).isEqualTo(dto.getTopic());
        assertThat(actualDto.getNsiPath()).isEqualTo(dto.getNsiPath());
        assertThat(actualDto.getEnabled()).isEqualTo(dto.getEnabled());
        assertThat(actualDto.getCodes()).isEqualTo(dto.getCodes());
    }

    @Test
    void updateTestBad() {
        // given
        final var notValidDto = DictionaryConfigDto.builder()
                .topic("")
                .nsiPath("/updated/updatedPath")
                .enabled(false)
                .codes(new Integer[]{101, 202, 303, 404})
                .build();

        // when
        // then
        assertThatThrownBy(() -> service.update(validEntity.getId(), notValidDto))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessage("update.dto.topic: поле не должно быть пустым");

        // given
        notValidDto.setTopic("validTopic");
        notValidDto.setNsiPath(null);

        // when
        // then
        assertThatThrownBy(() -> service.update(validEntity.getId(), notValidDto))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessage("update.dto.nsiPath: поле не должно быть пустым");

        // given
        notValidDto.setNsiPath("/updated/validPath");
        notValidDto.setEnabled(null);

        // when
        // then
        assertThatThrownBy(() -> service.update(validEntity.getId(), notValidDto))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessage("update.dto.enabled: поле не должно быть null");
    }

    @Test
    void deleteByIdTestOk() {
        // given
        final var entity = DictionaryConfig.builder()
                .topic(RandomStringUtils.randomAlphabetic(12))
                .nsiPath("/dict/path")
                .codes(List.of(101))
                .enabled(true)
                .build();
        repository.save(entity);

        service.deleteById(entity.getId());

        // when
        // then
        assertThatThrownBy(() -> service.findById(entity.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Не найден объект с id: [" + entity.getId() + "]");
    }

    @Test
    void deleteByIdTestNotFound() {
        assertThatThrownBy(() -> service.deleteById(Long.MAX_VALUE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Не найден объект с id: [" + Long.MAX_VALUE + "]");
    }

    @Test
    void findByTopicNameOk() {
        // given
        final var dto = service.findByTopic(validEntity.getTopic());

        // when
        // then
        assertThat(dto.getId()).isEqualTo(validEntity.getId());
        assertThat(dto.getTopic()).isEqualTo(validEntity.getTopic());
        assertThat(dto.getNsiPath()).isEqualTo(validEntity.getNsiPath());
        assertThat(dto.getEnabled()).isEqualTo(validEntity.getEnabled());
        assertThat(dto.getCodes()).isEqualTo(validEntity.getCodes().toArray());
    }

    @Test
    void findByTopicNameNotFound() {
        // given
        final var topicName = RandomStringUtils.randomAlphabetic(12);
        // when
        // then
        assertThatThrownBy(() -> service.findByTopic(topicName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Не найден объект с topic: [" + topicName + "]");
    }

    @Test
    void uniqueTopicNameTest() {
        // given
        final var entity = DictionaryConfig.builder()
                .topic("newTopicName")
                .nsiPath("newNsiPath")
                .enabled(true)
                .codes(List.of(444, 445, 446))
                .build();
        repository.save(entity);

        final var nonUniqueTopicDto = DictionaryConfigDto.builder()
                .topic(validEntity.getTopic())
                .nsiPath(entity.getNsiPath())
                .enabled(true)
                .codes(entity.getCodes().toArray(new Integer[0]))
                .build();

        // when
        // then
        assertThatThrownBy(() -> service.update(entity.getId(), nonUniqueTopicDto))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

}
