package com.ecommerce.order_service.exeption;

public class ServiceUnavailableException extends RuntimeException {

    public ServiceUnavailableException(String message){
        super(message);
    }

}
