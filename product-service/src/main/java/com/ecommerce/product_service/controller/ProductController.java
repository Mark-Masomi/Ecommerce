package com.ecommerce.product_service.controller;

import com.ecommerce.product_service.model.Product;
import com.ecommerce.product_service.repository.ProductRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@Tag(name="Product Controller", description="Operations related to products")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository){
        this.productRepository=productRepository;
    }
    @Operation(summary = "Get all products")
    @GetMapping
    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }

    @Operation(summary = "Find a specific product")
    @GetMapping("/{id}")
    public Product findProductById(@PathVariable Long id ){

        return productRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("product not found"));


    }

    @Operation(summary = "Find product by name")
    @ResponseStatus(HttpStatus.FOUND)
    @GetMapping("/name/{name}")
    public Product findProductByName(@PathVariable String name ){

        return productRepository.findByName(name);
    }

    @Operation(summary = "Find product by sku")
    @ResponseStatus(HttpStatus.FOUND)
    @GetMapping("/sku/{sku}")
    public Product findProductBySku (@PathVariable String sku){

        return productRepository.findBySku(sku);
    }

    @Operation(summary = "Create a new product")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product createProduct(@RequestBody Product product ){

        return productRepository.save(product);
    }

    @Operation(summary = "Update a existing product")
    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id,@RequestBody Product productDetails){
        Product product = productRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("product not found"));

        product.setName(productDetails.getName());
        product.setPrice(productDetails.getPrice());

        return productRepository.save(product);
        
    }

    @Operation(summary = "Delete a product")
    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id){

        productRepository.deleteById(id);
    }



}
