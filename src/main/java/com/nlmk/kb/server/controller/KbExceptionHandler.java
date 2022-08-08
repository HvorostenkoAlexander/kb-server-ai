package com.nlmk.kb.server.controller;

import com.nlmk.kb.server.exception.KafkaRestConfigException;
import com.nlmk.kb.server.exception.KafkaRestException;
import com.nlmk.kb.server.exception.ProductSenderException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class KbExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        log.error("handleException: {}", ex.getMessage());

        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleArgumentException(MethodArgumentNotValidException ex) {
        log.error("handleArgumentException: {}", ex.getMessage());

        final var errMap = ex.getBindingResult().getFieldErrors().stream().collect(
                Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage
                )
        );

        return new ResponseEntity<>(errMap, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProductSenderException.class)
    public ResponseEntity<String> handleProductSenderException(ProductSenderException ex) {
        log.error("handleProductSenderException: {}", ex.getMessage());
        // ошибки подготовки сообщения к отправке (ошибки внутри сервиса)
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(KafkaRestConfigException.class)
    public ResponseEntity<String> handleKafkaRestConfigException(KafkaRestConfigException ex) {
        log.error("handleKafkaRestConfigException: {}", ex.getMessage());
        // сервис не готов к отправке
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(KafkaRestException.class)
    public ResponseEntity<String> handleKafkaRestException(KafkaRestException ex) {
        log.error("handleKafkaRestException: {}", ex.getMessage());
        // ошибка отправки
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_GATEWAY);
    }

}
