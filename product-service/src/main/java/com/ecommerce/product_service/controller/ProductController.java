package com.ecommerce.product_service.controller;

import com.ecommerce.product_service.dto.CreateProductRequest;
import com.ecommerce.product_service.dto.ProductResponse;
import com.ecommerce.product_service.dto.StockUpdateRequest;
import com.ecommerce.product_service.dto.UpdateProductRequest;
import com.ecommerce.product_service.exception.InsufficientStockException;
import com.ecommerce.product_service.exception.ProductNotFoundException;
import com.ecommerce.product_service.model.Product;
import com.ecommerce.product_service.repository.ProductRepository;
import com.ecommerce.product_service.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@Tag(name="Product Controller", description="Operations related to products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService){
        this.productService = productService;

    }

    @Operation(summary = "Get all products")
    @GetMapping
    public List<ProductResponse> getAllProducts(){
        return productService.getAllProducts();
    }

    @Operation(summary = "Find a specific product by id")
    @GetMapping("/{id}")
    public ProductResponse getProductById(@PathVariable Long id ){

        return productService.getProductById(id);
    }


    @Operation(summary = "Find product by sku")
    @GetMapping("/sku/{sku}")
    public ProductResponse getProductBySku (@PathVariable String sku){

        return productService.getProductBySku(sku);
    }


    @Operation(summary = "Get stock quantity for a product")
    @GetMapping("/{id}/stock")
    public Integer getStock(@PathVariable Long id){
        return productService.getStock(id);
    }


    @Operation(summary = "Create a new product")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(@RequestBody CreateProductRequest request ){

        return productService.createProduct(request);
    }


    @Operation(summary = "Update an existing product")
    @PutMapping("/{id}")
    public ProductResponse updateProduct(@PathVariable Long id, @Valid @RequestBody UpdateProductRequest request){


        return productService.updateProduct(id,request);

    }


    @Operation(summary = "Update stock quantity for a product")
    @PutMapping("/{id}/stock")
    public ProductResponse updateStock(@PathVariable Long id,
                               @Valid @RequestBody StockUpdateRequest request){

        return productService.updateStock(id,request);
    }


    @Operation(summary = "Delete a product")
    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id){

        productRepository.deleteById(id);
    }



}
