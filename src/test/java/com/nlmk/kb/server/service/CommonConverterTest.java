package com.nlmk.kb.server.service;

import com.nlmk.kb.server.exception.DateTimeParseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CommonConverterTest {

    private final CommonConverter cct = new CommonConverterImpl();

    @Test
    void testParseDouble() {
        String str = "null";
        Double d = cct.parseToDouble(str);
        assertThat(d).isNull();

        str = null;
        d = cct.parseToDouble(str);
        assertThat(d).isNull();
    }

    @Test
    void testParseInteger() {
        String str = "null";
        Integer i = cct.parseToInteger(str);
        assertThat(i).isNull();

        str = null;
        i = cct.parseToInteger(str);
        assertThat(i).isNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2021-07-02T22:10:42.749-03:00",
            "2021-07-01T10:14:36+03:00",
            "2021-07-01T10:14:36"
    })
    void testParseToDate(String stringDate) {
        Date date = cct.parseToDate(stringDate);
        assertThat(date).isNotNull();

        System.out.println("date : " + date);
    }

    @Test
    void testParseToDateBad() {
        String stringDate = "2021-07-01";

        assertThatThrownBy(() -> cct.parseToDate(stringDate))
                .isInstanceOf(DateTimeParseException.class);
    }

}
