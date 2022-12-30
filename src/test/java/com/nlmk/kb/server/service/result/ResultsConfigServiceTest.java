package com.nlmk.kb.server.service.result;

import com.nlmk.kb.server.api.ResultsConfigDto;
import com.nlmk.kb.server.entity.ResultsConfig;
import com.nlmk.kb.server.mapper.ResultsConfigMapperImpl;
import com.nlmk.kb.server.repository.ResultsConfigRepository;
import com.nlmk.kb.server.service.result.configuration.ResultConfigService;
import com.nlmk.kb.server.service.result.configuration.ResultConfigServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({ResultConfigServiceImpl.class, ResultsConfigMapperImpl.class})
class ResultsConfigServiceTest {

    @Autowired
    private ResultsConfigRepository repository;
    @Autowired
    private ResultConfigService service;

    private ResultsConfig validResultConfig;
    private ResultsConfigDto validResultDto;

    @BeforeEach
    void setUp() {
        validResultConfig = repository.save(ResultsConfig.builder()
                .avroName("Avro Name #1")
                .condition("condition1")
                .topic("topic1")
                .enabled(true)
                .build());

        validResultDto = ResultsConfigDto.builder()
                .id(validResultConfig.getId())
                .avroName(validResultConfig.getAvroName())
                .condition(validResultConfig.getCondition())
                .topic(validResultConfig.getTopic())
                .enabled(validResultConfig.isEnabled())
                .build();
    }

    @Test
    void findByPageTest() {
        final var result = service.findPyPage(PageRequest.of(0, 10));
        assertNotNull(result);
        assertEquals(4 + 1, result.stream().toArray().length);
    }

    @Test
    void findByIdOk() {

        final var actualDto = service.findById(validResultConfig.getId());

        assertNotNull(actualDto);
        assertEquals(validResultConfig.getId(), actualDto.getId());
        assertEquals(validResultConfig.getAvroName(), actualDto.getAvroName());
        assertEquals(validResultConfig.getCondition(), actualDto.getCondition());
        assertEquals(validResultConfig.getTopic(), actualDto.getTopic());
        assertEquals(validResultConfig.isEnabled(), actualDto.isEnabled());
    }

    @Test
    void findByIdNotFound() {

        IllegalArgumentException iae = assertThrows(IllegalArgumentException.class,
                () -> service.findById(-111)
        );

        assertNotNull(iae);
        assertEquals("Не найден объект с id: [-111]", iae.getMessage());
    }

    @Test
    void createTestOk() {

        var newDto = ResultsConfigDto.builder()
                .id(null)
                .avroName(validResultConfig.getAvroName())
                .condition(validResultConfig.getCondition())
                .topic(validResultConfig.getTopic())
                .enabled(validResultConfig.isEnabled())
                .build();

        final var actualDto = service.create(newDto);

        assertNotNull(actualDto);
        assertNotNull(actualDto.getId());
        assertNotEquals(validResultConfig.getId(), actualDto.getId());
        assertEquals(validResultConfig.getAvroName(), actualDto.getAvroName());
        assertEquals(validResultConfig.getCondition(), actualDto.getCondition());
        assertEquals(validResultConfig.getTopic(), actualDto.getTopic());
        assertEquals(validResultConfig.isEnabled(), actualDto.isEnabled());
    }

    @Test
    void createTestBad() {
        IllegalArgumentException iae = assertThrows(IllegalArgumentException.class,
                () -> service.create(validResultDto)
        );

        assertNotNull(iae);
        assertEquals("При создании нового объекта id должен быть null", iae.getMessage());
    }

    @Test
    void updateTestOk() {

        validResultConfig.setCondition("new condition");
        validResultDto.setCondition("new condition");


        final var actualDto = service.update(validResultDto);

        assertNotNull(actualDto);
        assertEquals(validResultConfig.getId(), actualDto.getId());
        assertEquals(validResultConfig.getAvroName(), actualDto.getAvroName());
        assertEquals(validResultConfig.getCondition(), actualDto.getCondition());
        assertEquals(validResultConfig.getTopic(), actualDto.getTopic());
        assertEquals(validResultConfig.isEnabled(), actualDto.isEnabled());
    }

    @Test
    void updateTestBad() {
        validResultDto.setId(null);
        IllegalArgumentException iae = assertThrows(IllegalArgumentException.class,
                () -> service.update(validResultDto)
        );

        assertNotNull(iae);
        assertEquals("При изменении объекта id должен быть не null", iae.getMessage());
    }

    @Test
    void deleteByIdTestOk() {

        assertTrue(repository.findById(validResultConfig.getId()).isPresent());

        service.deleteById(validResultConfig.getId());

        assertTrue(repository.findById(validResultConfig.getId()).isEmpty());
    }

    @Test
    void deleteByIdBad() {

        IllegalArgumentException iae = null;
        try {
            service.deleteById(-111);
        } catch (IllegalArgumentException ex) {
            iae = ex;
        }

        assertNotNull(iae);
        assertEquals("Не найден объект с id: -111", iae.getMessage());
    }

}
