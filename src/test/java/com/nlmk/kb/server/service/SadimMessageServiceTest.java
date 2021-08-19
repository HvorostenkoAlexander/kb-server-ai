package com.nlmk.kb.server.service;

import com.nlmk.attestation.product.api.PreAttestationParamDto;
import com.nlmk.kb.server.entity.SadimMessage;
import com.nlmk.kb.server.repository.SadimMessageRepository;
import io.micrometer.core.instrument.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest
public class SadimMessageServiceTest {

    @Autowired
    private SadimMessageService service;

    @Autowired
    private SadimMessageRepository repository;

    @Autowired
    @Qualifier("sadimStreamApiParser")
    SadimJsonParser sadimJsonParser;

    private SadimMessage validMessage;

    private final String validSadimFilePath = "src/test/resources/json/sadim09052021.json";

    @BeforeEach
    void setUp() throws FileNotFoundException {

        final var sadimJson = getJsonFromPath(validSadimFilePath);
        final var attestationParam = sadimJsonParser.getParam(sadimJson);

        validMessage = SadimMessage.builder()
                .key("fakeKey")
                .partition(0)
                .offset(1L)
                .ts(LocalDateTime.now())
                .param(attestationParam.get())
                .build();

        repository.save(validMessage);
        System.out.println("validMessage: " + validMessage);
    }

    @AfterEach
    void tearDown() {
        repository.delete(validMessage);
        log.info("FINISH");
    }

    @ParameterizedTest
    @MethodSource("generateAttastationParamOk")
    void findByAttesstationParamTestOk(String pkId,
                                       String primeId,
                                       Integer meltNo,
                                       Integer lotNo) {
        var expectedParam = validMessage.getParam();

        var actualparamDto = service.findByAttesstationParam(pkId, primeId, meltNo, lotNo);

        assertNotNull(actualparamDto);
        assertEquals(expectedParam.getPrimeId(), actualparamDto.getPrimeId());
        assertEquals(expectedParam.getMeltNo(), actualparamDto.getMeltNo());
        assertEquals(expectedParam.getLotNo(), actualparamDto.getLotNo());
    }

    @ParameterizedTest
    @MethodSource("generateAttastationParamBad")
    void findByAttesstationParamTestBad(String pkId,
                                        String primeId,
                                        Integer meltNo,
                                        Integer lotNo) {

        IllegalArgumentException iae = assertThrows(IllegalArgumentException.class,
                () -> service.findByAttesstationParam(pkId, primeId, meltNo, lotNo
                ));
        assertNotNull(iae);
    }

    private String getJsonFromPath(String path) throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(new File(path));
        String stringTooLong = IOUtils.toString(fis);

        return stringTooLong;
    }

    private static Stream<Arguments> generateAttastationParamOk() {
        return Stream.of(
                Arguments.of("0001020210520101736225770", "0001020210520101736225770", 2111357, 40233),
                Arguments.of("0001020210520101736225770", "111", null, 40233),
                Arguments.of("12344", "0001020210520101736225770", 2111357, 40233),
                Arguments.of("11111", "0001020210520101736225770", 2111357, null),
                Arguments.of("123", "334", 2111357, 40233)
        );
    }

    private static Stream<Arguments> generateAttastationParamBad() {
        return Stream.of(
                Arguments.of("0001020210520101736225770", null, 2111357, 40233),
                Arguments.of(null, "0001020210520101736225770", 2111357, 40233),
                Arguments.of("123", "123", 555, 7777),
                Arguments.of("123", "123", null, 40233),
                Arguments.of("123", "123", null, 40233),
                Arguments.of("11111", "123", 2111357, null)
        );
    }
}
