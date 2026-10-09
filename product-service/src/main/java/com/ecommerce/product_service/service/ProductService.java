package com.ecommerce.product_service.service;

import com.ecommerce.product_service.dto.CreateProductRequest;
import com.ecommerce.product_service.dto.ProductResponse;
import com.ecommerce.product_service.dto.StockUpdateRequest;
import com.ecommerce.product_service.dto.UpdateProductRequest;

import java.util.List;

public interface ProductService {


    List<ProductResponse> getAllProducts();

    ProductResponse getProductById(Long id);

    ProductResponse getProductBySku(String sku);

    Integer getStock(Long id);

    ProductResponse createProduct(CreateProductRequest request);

    ProductResponse updateProduct(Long id, UpdateProductRequest request);

    ProductResponse updateStock(Long id, StockUpdateRequest request);

    void deleteProduct(Long id);

}
