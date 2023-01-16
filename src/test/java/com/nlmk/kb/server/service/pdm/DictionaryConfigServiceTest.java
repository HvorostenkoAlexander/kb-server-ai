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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
        final var dto = service.findById(validEntity.getId());

        assertNotNull(dto);
        assertEquals(dto.getId(), validEntity.getId());
        assertEquals(dto.getTopic(), validEntity.getTopic());
        assertEquals(dto.getNsiPath(), validEntity.getNsiPath());
        assertEquals(dto.getEnabled(), validEntity.getEnabled());
        assertArrayEquals(dto.getCodes(), validEntity.getCodes().toArray());
    }

    @Test
    void findByIdTestNotFound() {
        IllegalArgumentException iae = assertThrows(IllegalArgumentException.class,
                () -> service.findById(Long.MAX_VALUE)
        );

        assertNotNull(iae);
        assertEquals("Не найден объект с id: [" + Long.MAX_VALUE + "]", iae.getMessage());

    }

    @Test
    void updateTestOk() {

        final var dto = DictionaryConfigDto.builder()
                .topic("updatedTopic")
                .nsiPath("/updated/updatedPath")
                .enabled(false)
                .codes(new Integer[]{101, 202, 303, 404})
                .build();
        final var actualDto = service.update(validEntity.getId(), dto);

        assertNotNull(actualDto);
        assertEquals(validEntity.getId(), actualDto.getId());
        assertEquals(dto.getTopic(), actualDto.getTopic());
        assertEquals(dto.getNsiPath(), actualDto.getNsiPath());
        assertEquals(dto.getEnabled(), actualDto.getEnabled());
        assertArrayEquals(dto.getCodes(), actualDto.getCodes());
    }

    @Test
    void updateTestBad() {
        final var notValidDto = DictionaryConfigDto.builder()
                .topic("")
                .nsiPath("/updated/updatedPath")
                .enabled(false)
                .codes(new Integer[]{101, 202, 303, 404})
                .build();

        Exception ex = assertThrows(Exception.class,
                () -> service.update(validEntity.getId(), notValidDto)
        );
        assertNotNull(ex);
        assertEquals("update.dto.topic: поле не должно быть пустым", ex.getMessage());

        notValidDto.setTopic("validTopic");
        notValidDto.setNsiPath(null);

        ex = assertThrows(Exception.class,
                () -> service.update(validEntity.getId(), notValidDto)
        );
        assertNotNull(ex);
        assertEquals("update.dto.nsiPath: поле не должно быть пустым", ex.getMessage());

        notValidDto.setNsiPath("/updated/validPath");
        notValidDto.setEnabled(null);

        ex = assertThrows(Exception.class,
                () -> service.update(validEntity.getId(), notValidDto)
        );
        assertNotNull(ex);
        assertEquals("update.dto.enabled: поле не должно быть null", ex.getMessage());
    }

    @Test
    void deleteByIdTestOk() {
        final var entity = DictionaryConfig.builder()
                .topic(RandomStringUtils.randomAlphabetic(12))
                .nsiPath("/dict/path")
                .codes(List.of(101))
                .enabled(true)
                .build();
        repository.save(entity);

        service.deleteById(entity.getId());

        IllegalArgumentException iae = assertThrows(IllegalArgumentException.class,
                () -> service.findById(entity.getId())
        );

        assertNotNull(iae);
        assertEquals("Не найден объект с id: [" + entity.getId() + "]", iae.getMessage());
    }

    @Test
    void deleteByIdTestNotFound() {
        IllegalArgumentException iae = assertThrows(IllegalArgumentException.class,
                () -> service.deleteById(Long.MAX_VALUE)
        );

        assertNotNull(iae);
        assertEquals("Не найден объект с id: [" + Long.MAX_VALUE + "]", iae.getMessage());
    }

    @Test
    void findByTopicNameOk() {
        final var dto = service.findByTopic(validEntity.getTopic());

        assertNotNull(dto);
        assertEquals(dto.getId(), validEntity.getId());
        assertEquals(dto.getTopic(), validEntity.getTopic());
        assertEquals(dto.getNsiPath(), validEntity.getNsiPath());
        assertEquals(dto.getEnabled(), validEntity.getEnabled());
        assertArrayEquals(dto.getCodes(), validEntity.getCodes().toArray());
    }

    @Test
    void findByTopicNameNotFound() {
        final var topicName = RandomStringUtils.randomAlphabetic(12);

        IllegalArgumentException iae = assertThrows(IllegalArgumentException.class,
                () -> service.findByTopic(topicName)
        );
        assertNotNull(iae);
        assertEquals("Не найден объект с topic: [" + topicName + "]", iae.getMessage());
    }

    @Test
    void uniqueTopicNameTest() {
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

        DataIntegrityViolationException dive = assertThrows(DataIntegrityViolationException.class,
                () -> service.update(entity.getId(), nonUniqueTopicDto)
        );

        assertNotNull(dive);
    }

}
