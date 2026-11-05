package com.ecommerce.product_service.service;


import com.ecommerce.product_service.dto.CreateProductRequest;
import com.ecommerce.product_service.dto.ProductResponse;
import com.ecommerce.product_service.exception.ProductAlreadyExistsException;
import com.ecommerce.product_service.mapper.ProductMapper;
import com.ecommerce.product_service.model.Product;
import com.ecommerce.product_service.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductResponse productResponse;

    @BeforeEach
    void setUp(){
        product = Product.builder()
                .id(1L)
                .sku("IPHONE15-001")
                .name("iphone 15")
                .description("Flagship smartphone")
                .price(new BigDecimal("12990.00"))
                .stockQuantity(100)
                .available(true)
                .build();

        productResponse = ProductResponse.builder()
                .id(1L)
                .sku("IPHONE15-001")
                .name("iphone 15")
                .description("Flagship smartphone")
                .price(new BigDecimal("12990.00"))
                .stockQuantity(100)
                .available(true)
                .build();
    }

    // ==========================================================
    // CREATE
    // ==========================================================

    @Test
    @DisplayName("createProduct: should save product and return response")
    void createProduct_shouldSaveAndReturn(){
        CreateProductRequest request = CreateProductRequest.builder()
                .sku("IPHONE15-001")
                .name("iphone 15")
                .description("Flagship smartphone")
                .price(new BigDecimal("12990.00"))
                .stockQuantity(100)
                .build();

        Product mappedEntity = Product.builder()
                .sku(request.getSku())
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .build();

        when(productRepository.existsBySku("IPHONE15-001")).thenReturn(false);
        when(productMapper.toEntity(request)).thenReturn(mappedEntity);
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> {
            Product p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });
        when(productMapper.toResponse(any(Product.class))).thenReturn(productResponse
        );

        ProductResponse result = productService.createProduct(request);

        assertThat(result).isNotNull();
        assertThat(result.getSku()).isEqualTo("IPHONE15-001");
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("createProduct: should throw ProductAlreadyExistsException on duplicate sku")
    void createProduct_shouldThrow_whenSkuExists(){
        CreateProductRequest request = CreateProductRequest.builder()
                .sku("IPHONE15-001")
                .name("iphone 15")
                .price(new BigDecimal("12990.00"))
                .stockQuantity(100)
                .build();

        when(productRepository.existsBySku("IPHONE15-001")).thenReturn(true);

        assertThatThrownBy(() -> productService.createProduct(request))
                .isInstanceOf(ProductAlreadyExistsException.class)
                .hasMessageContaining("IPHONE15-001");

        verify(productRepository , never()).save(any());
        verify(productMapper, never()).toEntity(any());
    }

    @Test
    @DisplayName("createProduct: service must set available (mapper does not)")
    void createProduct_shouldSetAvailableInService (){
        CreateProductRequest request = CreateProductRequest.builder()
                .sku("IPHONE15-001")
                .name("iphone 15")
                .price(new BigDecimal("12990.00"))
                .stockQuantity(5)
                .build();

        //Mapper returns entity without available - this is the mappers contract
        Product mappedEntity = Product.builder()
                .sku("IPHONE15-001")
                .name("iphone 15")
                .price(new BigDecimal("12990.00"))
                .stockQuantity(5)
                .available(null)
                .build();

        when(productRepository.existsBySku("IPHONE15-001")).thenReturn(false);
        when(productMapper.toEntity(request)).thenReturn(mappedEntity);
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));
        when(productMapper.toResponse(any(Product.class))).thenReturn(productResponse);

        productService.createProduct(request);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        assertThat(captor.getValue().getAvailable())
                .as("service must calculate available = stockQuantity > 0")
                .isTrue();
    }

}
