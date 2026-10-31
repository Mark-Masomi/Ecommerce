package com.ecommerce.order_service;

import com.ecommerce.order_service.dto.OrderItemRequest;
import com.ecommerce.order_service.dto.OrderRequest;
import com.ecommerce.order_service.dto.OrderResponse;
import com.ecommerce.order_service.dto.ProductDto;
import com.ecommerce.order_service.feign.ProductServiceClient;
import com.ecommerce.order_service.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;


@SpringBootTest
@ActiveProfiles("test")
public class OrderServiceIntegrationTest {

    @Autowired
    private com.ecommerce.order_service.service.OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @MockitoBean
    private ProductServiceClient productServiceClient;

    private ProductDto productDto;

    @BeforeEach
    void setUp(){
        productDto = new ProductDto();
        productDto.setId(1L);
        productDto.setSku("IPHONE15-001");
        productDto.setName("iphone 15");
        productDto.setPrice(new BigDecimal("12990.00"));
        productDto.setStockQuantity(100);
        productDto.setAvailable(true);
    }

    @Test
    @DisplayName("Integration: create order and verify that it saves in database")
    void createOrder_shouldPersistOrder(){
        when(productServiceClient.getProductStock(1L)).thenReturn(100);
        when(productServiceClient.getProductById(1L)).thenReturn(productDto);

        OrderItemRequest item = new OrderItemRequest();
        item.setProductId(1L);
        item.setQuantity(2);

        OrderRequest request = new OrderRequest();
        request.setCustomerId("cust-123");
        request.setItems(List.of(item));

        OrderResponse response = orderService.createOrder(request);

        assertThat(response.getOrderNumber()).isNotBlank();
        assertThat(response.getTotalPrice()).isEqualByComparingTo(new BigDecimal("25980.00"));

        //Verify that order actually exists in database
        assertThat(orderRepository.findAll()).hasSize(1);
        assertThat(orderRepository.findAll().get(0).getCustomerId()).isEqualTo("cust-123");
    }

}
