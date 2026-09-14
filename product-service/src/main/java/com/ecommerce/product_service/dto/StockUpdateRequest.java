package com.ecommerce.product_service.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockUpdateRequest {
    private Integer quantityChange;     //positiv=add, negativ=reduce
}
