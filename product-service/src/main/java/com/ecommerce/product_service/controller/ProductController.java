package com.ecommerce.product_service.controller;

import com.ecommerce.product_service.dto.StockUpdateRequest;
import com.ecommerce.product_service.exception.InsufficientStockException;
import com.ecommerce.product_service.exception.ProductNotFoundException;
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
                .orElseThrow(()-> new ProductNotFoundException("product not found with id: "
                        + id));
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

    @Operation(summary = "Get stock quantity for a product")
    @GetMapping("/{id}/stock")
    public Integer getProductStock(@PathVariable Long id){

        Product product=productRepository.findById(id)
                .orElseThrow(()-> new ProductNotFoundException("Product not found with id: "
                        + id));

        return product.getStockQuantity();
    }

    @Operation(summary = "Update stock quantity for a product")
    @PutMapping("/{id}/stock")
    public Product updateStock(@PathVariable Long id,
                               @RequestBody StockUpdateRequest stockUpdate){
        Product product = productRepository.findById(id)
                .orElseThrow(()-> new ProductNotFoundException("Product not found with id: "+
                        id));
        int newStock= product.getStockQuantity()+ stockUpdate.getQuantityChange();

        if (newStock < 0){
            throw new InsufficientStockException(
                    "Cannot reduce bellow 0 for product: "+product.getSku()+
                            ". Current:"+ product.getStockQuantity()+
                    ", Requested change: "+ stockUpdate.getQuantityChange());


        }
        product.setStockQuantity(newStock);
        product.setAvailable(newStock > 0 );
        return productRepository.save(product);
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
