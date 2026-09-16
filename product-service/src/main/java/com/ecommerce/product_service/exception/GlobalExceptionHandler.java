package com.ecommerce.product_service.exception;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Map<String,Object>> handleProductNotFound(ProductNotFoundException ex){

        return buildResponse(HttpStatus.NOT_FOUND,ex.getMessage());
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<Map<String,Object>> handleInsufficientStock(InsufficientStockException
                                                                              ex){
        return buildResponse(HttpStatus.CONFLICT,ex.getMessage());
    }

    @ExceptionHandler(ProductAlreadyExistsException.class)
    public ResponseEntity<Map<String,Object>> handleProductAlreadyExists(ProductAlreadyExistsException
                                                                                 ex){
        return buildResponse(HttpStatus.CONFLICT,ex.getMessage());
    }

    /**
     * Concurrent modification of the same product (e.g., two orders
     * reducing the inventory at the same time). The @Version annotation
     * on Product causes Hibernate to throw an OptimisticLockException,
     * which Spring wraps into an
     * ObjectOptimisticLockingFailureException.
     *
     * Returns 409 CONFLICT so that the order service can retry.
     */
    @ExceptionHandler({ObjectOptimisticLockingFailureException.class,
    OptimisticLockingFailureException.class})
    public ResponseEntity<Map<String,Object>> handleOptimisticLock(Exception ex){
        return buildResponse(HttpStatus.CONFLICT,"Concurrent modification detected on product. "+
                "the resource was updated by another transaction. Please retry.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> handleValidation(MethodArgumentNotValidException ex){
        String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField()+": "+ err.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return buildResponse(HttpStatus.BAD_REQUEST,errors);
    }

    private ResponseEntity<Map<String,Object>> buildResponse(HttpStatus status, String message){
        Map<String,Object> body=new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status",status.value());
        body.put("error",status.getReasonPhrase());
        body.put("message",message);

        return ResponseEntity.status(status).body(body);
    }

}
