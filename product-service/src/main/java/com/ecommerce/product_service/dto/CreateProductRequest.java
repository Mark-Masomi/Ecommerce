package com.ecommerce.product_service.dto;


import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductRequest {

    @NotBlank(message = "Sku is required")
    @Size(min = 3,max = 30,message ="Sku must be between 3 and 30 characters")
    private String sku;

    @NotBlank(message = "Name is required")
    @Size(max = 255,message = "Name must not exceed 255 characters")
    private String name;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    @NotBlank(message = "Price is required")
    @Positive(message = "Price must be greater than 0")
    private BigDecimal price;


    @NotNull(message = "Stock quantity required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;
}
