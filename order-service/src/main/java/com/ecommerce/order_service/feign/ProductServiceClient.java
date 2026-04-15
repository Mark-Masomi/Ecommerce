package com.ecommerce.order_service.feign;

import com.ecommerce.order_service.dto.ProductDto;
import com.ecommerce.order_service.dto.StockUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "product-service",path = "/api/products")
public interface ProductServiceClient {

    @GetMapping("/{id}")
    ProductDto getProductById(@PathVariable("id") Long id);

    @GetMapping("/{id}/stock}")
    Integer getProductStock(@PathVariable("id") Long id);

    @PutMapping("/{id}/stock")
    void updateStock (@PathVariable("id") Long id, @RequestBody StockUpdateRequest stockUpdate);

}
