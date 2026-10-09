package com.ecommerce.order_service.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<Map<String,Object>> handleInsufficientStock(InsufficientStockException ex){

        return buildResponse(HttpStatus.CONFLICT,ex.getMessage());
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<Map<String, Object>> handleServiceUnavailable (ServiceUnavailableException ex){
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> handleValidation(MethodArgumentNotValidException ex)
    {

        String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return buildResponse(HttpStatus.BAD_REQUEST,errors);
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status,String message){
        Map<String,Object> body= new HashMap<>();
        body.put("timestamp",LocalDateTime.now());
        body.put("status",status.value());
        body.put("error",status.getReasonPhrase());
        body.put("message",message);
        return ResponseEntity.status(status).body(body);
    }

 }
