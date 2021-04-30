package com.nlmk.kb.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nlmk.l3.ccm.pgp.AttestationRequest;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class PamService {

    @Value("${pam.url}")
    private String pamUrl;

    final private RestTemplate restTemplate;

    public void postAttestationRequest(AttestationRequest request) {

        String jsonString = convertToJsonString(request);

        System.out.println("--- jsonString: "+jsonString);

       // restTemplate.postForEntity(pamUrl, jsonString, String.class);
    }

    private <T extends GenericRecord> String convertToJsonString(T event)  {

        String jsonstring = "";

        try {
            DatumWriter<T> writer = new GenericDatumWriter<T>(event.getSchema());
            OutputStream out = new ByteArrayOutputStream();
            JsonEncoder encoder = EncoderFactory.get().jsonEncoder(event.getSchema(), out);
            writer.write(event, encoder);
            encoder.flush();
            jsonstring = out.toString();
        } catch (IOException e) {
            log.error("IOException occurred.", e);
        }

        return jsonstring;
    }
}
