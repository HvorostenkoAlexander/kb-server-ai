package com.nlmk.kb.server.service.zifra;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.kb.server.service.CommonConverterImpl;
import com.nlmk.kb.server.service.zifra.creators.SpCustomerGroupMdmCreator;
import com.nlmk.kb.server.service.zifra.creators.SpCustomerMdmCreator;
import com.nlmk.kb.server.service.zifra.creators.SpGroupAndCustomerMdmCreator;
import nlmk.l3.nsi.zifra.Reason;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({
        MdmMessageServiceImpl.class,
        MdmMessageConverterImpl.class,
        MdmDictionaryCreatorImpl.class,
        CommonConverterImpl.class,
        SpCustomerMdmCreator.class,
        SpCustomerGroupMdmCreator.class,
        SpGroupAndCustomerMdmCreator.class
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MdmMessageServiceTest {

    @Autowired
    private MdmMessageService messageService;

    @Autowired
    private MdmMessageConverter messageConverter;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static String spCustomerTopic;
    private static String spCustomerGroupTopic;
    private static String spGroupAndCustomerTopic;

    private final static String spCustomerGroupJsonPath = "src/test/resources/json/SpCustomerGroupExample.json";
    private final static String spGroupAndCustomerJsonPath = "src/test/resources/json/SpGroupAndCustomerExample.json";
    private final static String spCustomerJsonPath = "src/test/resources/json/SpCustomerExample.json";

    @BeforeAll
    public void setTopicNames(
            @Value("${kafka.zifra.topic.sp-customer}") String spCustomer,
            @Value("${kafka.zifra.topic.sp-customer-group}") String spCustomerGroup,
            @Value("${kafka.zifra.topic.sp-group-and-customer}") String spGroupAndCustomer
    ) {
        MdmMessageServiceTest.spCustomerTopic = spCustomer;
        MdmMessageServiceTest.spCustomerGroupTopic = spCustomerGroup;
        MdmMessageServiceTest.spGroupAndCustomerTopic = spGroupAndCustomer;
    }

    public static Stream<Arguments> provideAttestationParameter() {
        return Stream.of(
                Arguments.of(spCustomerTopic, spCustomerJsonPath),
                Arguments.of(spCustomerGroupTopic, spCustomerGroupJsonPath),
                Arguments.of(spGroupAndCustomerTopic, spGroupAndCustomerJsonPath)
        );
    }

    private ConsumerRecord<Object, Object> prepareConsumerRecord(String topic, String path) throws IOException {
        final var json = new String(Files.readAllBytes(Path.of(path)));
        final var value = objectMapper.readValue(json, Reason.class);
        final var key = "key~" + Instant.now().getEpochSecond();

        return new ConsumerRecord<>(topic, 0, 0, key, value);
    }

    @ParameterizedTest
    @MethodSource("provideAttestationParameter")
    void parseSaveUpdate(String topic, String path) throws Exception {

        final var spCustomerRecord = prepareConsumerRecord(topic, path);
        assertThat(spCustomerRecord).isNotNull();

        final var message = messageConverter.fromConsumerRecord(spCustomerRecord);
        final var result = messageService.save(message);

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getNote()).isNull();

        result.get().setNote("OK");
        final var result2 = messageService.update(result.get());

        assertThat(result2.isPresent()).isTrue();
        assertThat(result2.get().getNote()).isEqualTo("OK");
    }

    @Test
    void shouldReturnRecentIfRecordWithSameTopicPartitionOffsetAlreadyExists() throws IOException {

        final var spCustomerRecord = prepareConsumerRecord(spCustomerTopic, spCustomerJsonPath);
        assertThat(spCustomerRecord).isNotNull();

        final var message = messageConverter.fromConsumerRecord(spCustomerRecord);
        message.setNote("TEST1");
        final var result = messageService.save(message);

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getNote()).isEqualTo("TEST1");

        final var message2 = messageConverter.fromConsumerRecord(spCustomerRecord);
        message2.setNote("TEST2"); // другая метка
        final var result2 = messageService.save(message2);

        // возвращается старая запись
        assertThat(result2.isPresent()).isTrue();
        assertThat(result2.get().getNote()).isEqualTo("TEST1");

    }


}
