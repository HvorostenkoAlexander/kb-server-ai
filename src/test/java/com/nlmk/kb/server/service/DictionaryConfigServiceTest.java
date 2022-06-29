package com.nlmk.kb.server.service;

import com.nlmk.kb.server.dto.DictionaryConfigDto;
import com.nlmk.kb.server.entity.DictionaryConfig;
import com.nlmk.kb.server.repository.DictionaryConfigRepository;
import com.nlmk.kb.server.service.pdm.DictionaryConfigService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
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
        log.info("START validEntity:{}", validEntity);
    }

    @AfterEach
    void tearDown() {
        repository.deleteById(validEntity.getId());
        log.info("FINISH");
    }

    @Test
    void findDtoByIdTestOk() {
        val dto = service.findById(validEntity.getId());

        assertNotNull(dto);
        assertEquals(dto.getId(), validEntity.getId());
        assertEquals(dto.getTopic(), validEntity.getTopic());
        assertEquals(dto.getNsiPath(), validEntity.getNsiPath());
        assertEquals(dto.getEnabled(), validEntity.getEnabled());
        assertTrue(Arrays.equals(dto.getCodes(), validEntity.getCodes().toArray()));
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

        val dto = DictionaryConfigDto.builder()
                .topic("updatedTopic")
                .nsiPath("/updated/updatedPath")
                .enabled(false)
                .codes(new Integer[]{101, 202, 303, 404})
                .build();
        val actualDto = service.update(validEntity.getId(), dto);

        assertNotNull(actualDto);
        assertEquals(validEntity.getId(), actualDto.getId());
        assertEquals(dto.getTopic(), actualDto.getTopic());
        assertEquals(dto.getNsiPath(), actualDto.getNsiPath());
        assertEquals(dto.getEnabled(), actualDto.getEnabled());
        assertTrue(Arrays.equals(dto.getCodes(), actualDto.getCodes()));
    }

    @Test
    void updateTestBad() {
        val notValidDto = DictionaryConfigDto.builder()
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
        val entity = DictionaryConfig.builder()
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
        val dto = service.findByTopic(validEntity.getTopic());

        assertNotNull(dto);
        assertEquals(dto.getId(), validEntity.getId());
        assertEquals(dto.getTopic(), validEntity.getTopic());
        assertEquals(dto.getNsiPath(), validEntity.getNsiPath());
        assertEquals(dto.getEnabled(), validEntity.getEnabled());
        assertTrue(Arrays.equals(dto.getCodes(), validEntity.getCodes().toArray()));
    }

    @Test
    void findByTopicNameNotFound() {
        val topicName = RandomStringUtils.randomAlphabetic(12);

        IllegalArgumentException iae = assertThrows(IllegalArgumentException.class,
                () -> service.findByTopic(topicName)
        );
        assertNotNull(iae);
        assertEquals("Не найден объект с topic: [" + topicName + "]", iae.getMessage());
    }

    @Test
    void uniqueTopicNameTest() {
        val entity = DictionaryConfig.builder()
                .topic("newTopicName")
                .nsiPath("newNsiPath")
                .enabled(true)
                .codes(List.of(444, 445, 446))
                .build();
        repository.save(entity);

        val nonUniqueTopicDto = DictionaryConfigDto.builder()
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
