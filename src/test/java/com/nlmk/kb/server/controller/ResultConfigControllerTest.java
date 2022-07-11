package com.nlmk.kb.server.controller;

import com.nlmk.kb.server.service.result_config.AvroVersionService;
import com.nlmk.kb.server.service.result_config.ResultConfigService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(ResultConfigController.class)
class ResultConfigControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private ResultConfigService resultConfigService;
    @MockBean
    private AvroVersionService avroVersionService;

    @Test
    void getResultConfig() throws Exception {
        {
            final var url = "/configuration/avro";
            mvc.perform(MockMvcRequestBuilders.get(url))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$").isArray());
        }
        {
            final var url = "/configuration/topics";
            mvc.perform(MockMvcRequestBuilders.get(url))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().bytes("".getBytes()));
        }
        {
            final var url = "/configuration/topics/1";
            mvc.perform(MockMvcRequestBuilders.get(url))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().bytes("".getBytes()));
        }
    }

    @Test
    void postResultConfig() throws Exception {
        final var url = "/configuration/topics";

        mvc.perform(MockMvcRequestBuilders.post(url)
                        .header(HttpHeaders.AUTHORIZATION, "T V")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        mvc.perform(MockMvcRequestBuilders.post(url)
                        .header(HttpHeaders.AUTHORIZATION, "T V")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"topic\":\"t1\", \"avroName\":\"a1\"}"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().bytes("".getBytes()));
    }

    @Test
    void putResultConfig() throws Exception {
        final var url = "/configuration/topics/1";

        mvc.perform(MockMvcRequestBuilders.put(url)
                        .header(HttpHeaders.AUTHORIZATION, "T V")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        mvc.perform(MockMvcRequestBuilders.put(url)
                        .header(HttpHeaders.AUTHORIZATION, "T V")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"topic\":\"t1\", \"avroName\":\"a1\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().bytes("".getBytes()));
    }

    @Test
    void deleteResultConfig() throws Exception {
        final var url = "/configuration/topics/1";

        mvc.perform(MockMvcRequestBuilders.delete(url)
                        .header(HttpHeaders.AUTHORIZATION, "T V"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().bytes("1".getBytes()));
    }

}
