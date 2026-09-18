package com.ecommerce.product_service.service;

import com.ecommerce.product_service.dto.CreateProductRequest;
import com.ecommerce.product_service.dto.ProductResponse;
import com.ecommerce.product_service.dto.StockUpdateRequest;
import com.ecommerce.product_service.dto.UpdateProductRequest;
import com.ecommerce.product_service.exception.ProductAlreadyExistsException;
import com.ecommerce.product_service.exception.ProductNotFoundException;
import com.ecommerce.product_service.mapper.ProductMapper;
import com.ecommerce.product_service.model.Product;
import com.ecommerce.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;


    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        return productMapper.toResponse(findProductOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductBySku(String sku) {
        Product product = productRepository.findBySku(sku)
                .orElseThrow(()-> new ProductNotFoundException
                        ("Product not found with SKU: " + sku) );
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getStock(Long id) {

        return findProductOrThrow(id).getStockQuantity();
    }

    // ==========================================================
    // WRITE
    // ==========================================================


    @Override
    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
       if (productRepository.existsBySku(request.getSku())){
           throw new ProductAlreadyExistsException
                   ("Product already exists with sku: " + request.getSku());
       }

       Product product = productMapper.toEntity(request);
       recalculateAvailability(product);

       Product saved =productRepository.save(product);

        return productMapper.toResponse(saved);
    }

    @Override
    public ProductResponse updateProduct(Long id, UpdateProductRequest request) {
        return null;
    }

    @Override
    public ProductResponse updateStock(Long id, StockUpdateRequest request) {
        return null;
    }

    @Override
    public void deleteProduct(Long id) {

    }

    // ==========================================================
    // DOMAIN / HELPERS
    // ==========================================================

    /**
     * Business rule: a product is available only when the stock quantity is > 0.
     * Domain logic belong in the service layer, not in the mapper
     */

    private void recalculateAvailability(Product product){
        boolean available = product.getStockQuantity() != null
                && product.getStockQuantity() > 0;
        product.setAvailable(available);
    }


    private Product findProductOrThrow(Long id){
        return productRepository.findById(id)
                .orElseThrow(()-> new ProductNotFoundException(
                        "Product not found with id: "+id
                ));
    }

}
