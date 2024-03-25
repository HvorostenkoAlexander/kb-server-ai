package com.nlmk.kb.server.service.sadim;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.kb.server.service.CommonConverter;
import com.nlmk.kb.server.service.CommonConverterImpl;
import io.micrometer.core.instrument.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import nlmk.sadim.Sadim;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class SadimJsonParserTest {

    private final CommonConverter commonConverter = new CommonConverterImpl();
    private final SadimJsonParser parser = new SadimJsonParserImpl(commonConverter);

    @Test
    void enumFiled() {
        assertThat(SadimJsonElement.fromName(null)).isNull();
        assertThat(SadimJsonElement.fromName("")).isNull();
        assertThat(SadimJsonElement.fromName(" ")).isNull();
        assertThat(SadimJsonElement.fromName("A")).isNull();

        assertThat(SadimJsonElement.fromName("lot_no")).isEqualTo(SadimJsonElement.LOT_NO);
    }

    @Test
    void parsing() throws FileNotFoundException {
        final var sadimJson = getJsonFromPath();
        final var attestationParam = parser.getParam(sadimJson);

        assertThat(attestationParam).isPresent().hasValueSatisfying(param -> {
            assertThat(param.getPrimeId()).isEqualTo("0001020210520101736225770");
            assertThat(param.getLotNo()).isEqualTo(40233);
            assertThat(param.getMeltNo()).isEqualTo(2111357);
            assertThat(param.getT12Min()).isEqualTo(825);
            assertThat(param.getT12Max()).isEqualTo(865);
            assertThat(param.getTcmMin()).isEqualTo(615);
            assertThat(param.getTcmMax()).isEqualTo(665);
            assertThat(param.getPbi()).isEqualTo(100);
            assertThat(param.getProfFact()).isEqualTo(25);
            assertThat(param.getWedgeFact()).isEqualTo(5);
            assertThat(param.getPh1sgp()).isEqualTo(100);
            assertThat(param.getPh12sgp()).isEqualTo("26.62");
            assertThat(param.getPh23sgp()).isEqualTo(90.27);
            assertThat(param.getSqcCritMax()).isEqualTo(4);
            assertThat(param.getLclThckng()).isEqualTo("5.0;9.0;6.0;4.0;2.0;12.0;4.0;2.0");
        });
    }

    private String getJsonFromPath() throws FileNotFoundException {
        final String validSadimFilePath = "src/test/resources/json/sadim09052021.json";
        FileInputStream fis = new FileInputStream(validSadimFilePath);
        return IOUtils.toString(fis);
    }

    @Test
    void sadimJsonTest() throws IOException {
        Sadim value = new ObjectMapper()
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
                .readValue(getClass().getClassLoader()
                        .getResourceAsStream("json/exampleFromSadim.json"), Sadim.class);
        assertThat(value).isNotNull();
    }

    @Test
    void SadimStreamApiParserTest() throws FileNotFoundException {
        FileInputStream fis = new FileInputStream("src/test/resources/json/sadim09052020_1.json");
        final var jsonString = IOUtils.toString(fis);
        final var param = parser.getParam(jsonString);
        log.info("--- param: " + param);
        assertThat(param).isPresent();
    }
}
