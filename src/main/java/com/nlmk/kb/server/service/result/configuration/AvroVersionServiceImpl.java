package com.nlmk.kb.server.service.result.configuration;

import com.nlmk.kb.server.entity.AvroVersion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvroVersionServiceImpl implements AvroVersionService {

    private final List<ApcsAvro> apcsAvros;

    @Override
    public List<AvroVersion> findAll() {
        return apcsAvros.stream().map(a ->
                AvroVersion.builder()
                        .name(a.getName())
                        .description(a.getDescription())
                        .data(a.getData())
                        .build()
        ).collect(Collectors.toList());
    }

}
