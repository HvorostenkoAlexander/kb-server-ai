package com.nlmk.kb.server.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import nlmk.sadim.Sadim;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SadimTest {

    @Test
    void sadimJsonTest() throws IOException {
        String testString = "2021-04-28T16:29:29.612-03:00";
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

        Sadim value = new ObjectMapper()
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
                .readValue(getClass().getClassLoader()
                        .getResourceAsStream("json/exampleFromSadim.json"), Sadim.class);

        System.out.println("---sadim: "+value);
        assertNotNull(value);
    }

    @Test
    void test(){

        int i=0;

        while (true) {
            if (i==5) {
                System.out.println("i=5");
                break;
            }
            i++;
        }

        System.out.println("end");
    }
}
