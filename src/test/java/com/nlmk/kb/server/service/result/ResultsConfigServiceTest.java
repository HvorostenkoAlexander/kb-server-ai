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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        assertThat(result.stream().toArray().length).isEqualTo(4 + 1);
    }

    @Test
    void findByIdOk() {

        final var actualDto = service.findById(validResultConfig.getId());

        assertThat(actualDto.getId()).isEqualTo(validResultConfig.getId());
        assertThat(actualDto.getAvroName()).isEqualTo(validResultConfig.getAvroName());
        assertThat(actualDto.getCondition()).isEqualTo(validResultConfig.getCondition());
        assertThat(actualDto.getTopic()).isEqualTo(validResultConfig.getTopic());
        assertThat(actualDto.isEnabled()).isEqualTo(validResultConfig.isEnabled());
    }

    @Test
    void findByIdNotFound() {
        assertThatThrownBy(() -> service.findById(-111))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Не найден объект с id: [-111]");
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

        assertThat(actualDto.getId()).isNotNull();
        assertThat(actualDto.getId()).isNotEqualTo(validResultConfig.getId());
        assertThat(actualDto.getAvroName()).isEqualTo(validResultConfig.getAvroName());
        assertThat(actualDto.getCondition()).isEqualTo(validResultConfig.getCondition());
        assertThat(actualDto.getTopic()).isEqualTo(validResultConfig.getTopic());
        assertThat(actualDto.isEnabled()).isEqualTo(validResultConfig.isEnabled());
    }

    @Test
    void createTestBad() {
        assertThatThrownBy(() -> service.create(validResultDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("При создании нового объекта id должен быть null");
    }

    @Test
    void updateTestOk() {

        validResultConfig.setCondition("new condition");
        validResultDto.setCondition("new condition");


        final var actualDto = service.update(validResultDto);

        assertThat(actualDto.getId()).isEqualTo(validResultConfig.getId());
        assertThat(actualDto.getAvroName()).isEqualTo(validResultConfig.getAvroName());
        assertThat(actualDto.getCondition()).isEqualTo(validResultConfig.getCondition());
        assertThat(actualDto.getTopic()).isEqualTo(validResultConfig.getTopic());
        assertThat(actualDto.isEnabled()).isEqualTo(validResultConfig.isEnabled());
    }

    @Test
    void updateTestBad() {
        validResultDto.setId(null);
        assertThatThrownBy(() -> service.update(validResultDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("При изменении объекта id должен быть не null");
    }

    @Test
    void deleteByIdTestOk() {
        assertThat(repository.findById(validResultConfig.getId())).isPresent();

        service.deleteById(validResultConfig.getId());

        assertThat(repository.findById(validResultConfig.getId())).isEmpty();
    }

    @Test
    void deleteByIdBad() {
        assertThatThrownBy(() -> service.deleteById(-111))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Не найден объект с id: -111");
    }

}
