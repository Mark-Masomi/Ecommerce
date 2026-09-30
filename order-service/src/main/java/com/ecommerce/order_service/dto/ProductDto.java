package com.ecommerce.order_service.dto;

import lombok.Data;

import java.math.BigDecimal;
/**
 * Mirrors ProductResponse in the product-service.
 * The field is called available (not isAvailable) to match the JSON contract.
 */
@Data
public class ProductDto {

    private Long id;
    private String sku;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private Boolean available;



}
