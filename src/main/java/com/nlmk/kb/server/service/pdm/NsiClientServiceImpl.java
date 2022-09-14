package com.nlmk.kb.server.service.pdm;

import com.nlmk.kb.server.entity.pdm.PdmMessage;
import com.nlmk.kb.server.service.pdm.senders.PdmMessageSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Slf4j
@Service
public class NsiClientServiceImpl implements NsiClientService {

    private final Map<String, PdmMessageSender> senders;

    public NsiClientServiceImpl(List<PdmMessageSender> allSenders) {
        this.senders = allSenders.stream().collect(toMap(PdmMessageSender::getType, Function.identity()));
    }

    @Override
    public ResponseEntity<Long> sendPdmMessage(PdmMessage message) {

        PdmMessageSender sender = senders.get(message.getTopic());

        if (sender == null) {
            throw new IllegalArgumentException("Не поддерживается отправка сообщений в nsi-server для топика: " + message.getTopic());
        }
        return sender.send(message);
    }
}
