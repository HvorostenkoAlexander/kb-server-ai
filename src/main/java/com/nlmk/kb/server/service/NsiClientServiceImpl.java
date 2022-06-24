package com.nlmk.kb.server.service;

import com.nlmk.kb.server.entity.pdm.PdmMessage;
import com.nlmk.kb.server.service.pdm.senders.MessageSender;
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

    private final Map<String, MessageSender> senders;

    public NsiClientServiceImpl(List<MessageSender> allSenders) {
        this.senders = allSenders.stream().collect(toMap(MessageSender::getType, Function.identity()));
    }

    @Override
    public ResponseEntity<Long> sendPdmMessage(PdmMessage message) {

        MessageSender sender = senders.get(message.getTopic());

        if (sender == null) {
            throw new IllegalArgumentException("Не поддерживается отправка сообщений в nsi-server для топика: " + message.getTopic());
        }
        return sender.send(message);
    }
}
