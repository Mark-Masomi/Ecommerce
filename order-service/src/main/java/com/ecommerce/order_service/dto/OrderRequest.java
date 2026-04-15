package com.ecommerce.order_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Data
public class OrderRequest {

    @NotNull(message = "Costumer Id cannot be null")
    private String customerId;

    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderItemRequest> items;
}
