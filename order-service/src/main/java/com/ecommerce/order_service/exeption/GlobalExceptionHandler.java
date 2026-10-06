package com.ecommerce.order_service.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {



    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status,String message){
        Map<String,Object> body= new HashMap<>();
        body.put("timestamp",LocalDateTime.now());
        body.put("status",status.value());
        body.put("error",status.getReasonPhrase());
        body.put("message",message);
        return ResponseEntity.status(status).body(body);
    }

 }
