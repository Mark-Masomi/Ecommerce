package com.ecommerce.order_service.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    private String productId;  //Reference to product-service
    private String sku;
    private Integer quantity;
    private BigDecimal unitPrice;
}
