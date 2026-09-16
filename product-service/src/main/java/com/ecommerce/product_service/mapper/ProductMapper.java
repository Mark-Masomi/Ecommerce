package com.ecommerce.product_service.mapper;

import com.ecommerce.product_service.dto.CreateProductRequest;
import com.ecommerce.product_service.dto.ProductResponse;
import com.ecommerce.product_service.dto.UpdateProductRequest;
import com.ecommerce.product_service.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    /**
     * Maps request -> entity
     * OBS: available is not set here - its business logic and
     * gets handled by ProductService
     */

    public Product toEntity(CreateProductRequest request){
        return Product.builder()
                .sku(request.getSku())
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .build();
    }

    /**
     *updates fields of a managed entity
     * OBS. Available is not set here - handles of ProductService
     */
    public void updateEntity(Product product, UpdateProductRequest request){

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());

    }


    public ProductResponse toResponse(Product product){
        return ProductResponse.builder()
                .id(product.getId())
                .sku(product.getSku())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .available(product.getAvailable())
                .build();

    }


}
