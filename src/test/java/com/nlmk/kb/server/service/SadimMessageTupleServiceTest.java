package com.nlmk.kb.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nlmk.attestation.product.api.PreAttestationParamDto;
import com.nlmk.kb.server.mock.MockTuple;
import com.nlmk.kb.server.repository.SadimMessageRepository;
import com.nlmk.kb.server.service.impl.CommonConverterImpl;
import com.nlmk.kb.server.service.impl.DtoConverterImpl;
import com.nlmk.kb.server.service.impl.SadimMessageServiceImpl;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import javax.persistence.Tuple;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class SadimMessageTupleServiceTest {

    @Mock
    private SadimMessageRepository repository;

    @Mock
    private CommonConverter converter;

    @Mock
    private DtoConverter dtoConverter;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private SadimMessageServiceImpl service;

    private Page<Tuple> validTuples;

    private PreAttestationParamDto paramDto;

    @BeforeEach
    void setUp() {
        Map<String, String> t = new HashMap<>() {{
            put("ts", LocalDateTime.now().toString());
            put("id", String.valueOf(RandomUtils.nextLong(10L, 50L)));
            put("prime_id", "000323434234");
            put("t12_min", String.valueOf(RandomUtils.nextDouble(0, 10)));
            put("t12_max", String.valueOf(RandomUtils.nextDouble(0, 10)));
            put("tcm_min", String.valueOf(RandomUtils.nextDouble(0, 10)));
            put("tcm_max", String.valueOf(RandomUtils.nextDouble(0, 10)));
            put("pbi", String.valueOf(RandomUtils.nextDouble(0, 10)));
            put("prof_fact", String.valueOf(RandomUtils.nextDouble(0, 10)));
            put("wedge_fact", String.valueOf(RandomUtils.nextDouble(0, 10)));
            put("sqc_crit_max", String.valueOf(RandomUtils.nextDouble(0, 10)));
            put("ph1_sgp", String.valueOf(RandomUtils.nextDouble(0, 10)));
            put("ph12_sgp", String.valueOf(RandomUtils.nextDouble(0, 10)));
            put("ph23_sgp", String.valueOf(RandomUtils.nextDouble(0, 10)));
            put("estimate", String.valueOf(RandomUtils.nextInt(0, 10)));
            put("lot_no", String.valueOf(RandomUtils.nextInt(0, 10)));
            put("melt_no", String.valueOf(RandomUtils.nextInt(0, 10)));
            put("lclthckng", "12.0;34.0;5.0;3.0");
        }};

        Tuple tuple = new MockTuple(t);

        validTuples = new PageImpl<>(List.of(tuple));

        CommonConverter commonConverter = new CommonConverterImpl();
        paramDto = new DtoConverterImpl(commonConverter).toPreAttestationParamDto(
                validTuples.getContent().get(0)
        );
    }

    @Test
    void findPageByParamTestOk() {
        given(dtoConverter.toPreAttestationParamDto(any(Tuple.class))).willReturn(paramDto);
        given(converter.getByTupleAlias(validTuples.getContent().get(0), "ts")).willReturn(LocalDateTime.now().toString());
        given(objectMapper.createObjectNode()).willReturn(new ObjectMapper().createObjectNode());
        given(converter.parseToStringByDatePattern(any(Date.class), any(String.class))).willReturn("2021-08-01");
        given(repository.findPreAttestationTuplesByParam(any(String.class),
                any(String.class),
                any(String.class),
                any(Integer.class),
                any(Integer.class),
                any(PageRequest.class))
        ).willReturn(validTuples);

        Page<ObjectNode> json = service.findPageByParam("", 0, 0, new Date(), new Date(), PageRequest.of(0, 10));

        System.out.println("--- json: " + json.getContent().toString());

        then(repository).should(times(0)).findPreAttestationTuplesByDates(any(String.class),
                any(String.class),
                any(PageRequest.class));
        then(repository).should(times(1)).findPreAttestationTuplesByParam(any(String.class),
                any(String.class),
                any(String.class),
                any(Integer.class),
                any(Integer.class),
                any(PageRequest.class));
        assertNotNull(json);
        assertFalse(json.getContent().isEmpty());
    }

    @Test
    void findPageByDateTestOk() {
        given(dtoConverter.toPreAttestationParamDto(any(Tuple.class))).willReturn(paramDto);
        given(converter.getByTupleAlias(validTuples.getContent().get(0), "ts")).willReturn(LocalDateTime.now().toString());
        given(objectMapper.createObjectNode()).willReturn(new ObjectMapper().createObjectNode());
        given(converter.parseToStringByDatePattern(any(Date.class), any(String.class))).willReturn("2021-08-01");
        given(repository.findPreAttestationTuplesByDates(any(String.class),any(String.class),any(PageRequest.class))
        ).willReturn(validTuples);

        Page<ObjectNode> json = service.findPageByParam(null, null, null, new Date(), new Date(), PageRequest.of(0, 10));

        System.out.println("--- json: " + json.getContent().toString());

        then(repository).should(times(1)).findPreAttestationTuplesByDates(any(String.class),
                any(String.class),
                any(PageRequest.class));
        then(repository).should(times(0)).findPreAttestationTuplesByParam(any(String.class),
                any(String.class),
                any(String.class),
                any(Integer.class),
                any(Integer.class),
                any(PageRequest.class));
        assertNotNull(json);
        assertFalse(json.getContent().isEmpty());
    }

}
