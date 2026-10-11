package com.ecommerce.order_service.service;

import com.ecommerce.order_service.dto.*;
import com.ecommerce.order_service.feign.ProductServiceClient;
import com.ecommerce.order_service.model.Order;
import com.ecommerce.order_service.model.OrderStatus;
import com.ecommerce.order_service.repository.OrderRepository;
import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductServiceClient productServiceClient;

    @InjectMocks
    private OrderServiceImpl orderService;

    private ProductDto productDto;

    @BeforeEach
    void setUp() {
        productDto = new ProductDto();
        productDto.setId(1L);
        productDto.setSku("IPHONE15-001");
        productDto.setName("iPhone 15");
        productDto.setPrice(new BigDecimal("12990.00"));
        productDto.setStockQuantity(100);
        productDto.setAvailable(true);
    }

    // ==========================================================
    // HAPPY PATH
    // ==========================================================

    @Test
    @DisplayName("createOrder: ska skapa order och uppdatera lager vid tillräckligt saldo")
    void createOrder_shouldCreateOrderAndUpdateStock_whenStockIsSufficient() {
        // Arrange
        OrderRequest request = buildOrderRequest(1L, 2);
        when(productServiceClient.getProductStock(1L)).thenReturn(100);
        when(productServiceClient.getProductById(1L)).thenReturn(productDto);
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setId(1L);
            return o;
        });

        // Act
        OrderResponse response = orderService.createOrder(request);

        // Assert – response
        assertThat(response).isNotNull();
        assertThat(response.getOrderNumber()).isNotBlank();
        assertThat(response.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(response.getTotalPrice()).isEqualByComparingTo(new BigDecimal("25980.00"));
        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getSku()).isEqualTo("IPHONE15-001");
        assertThat(response.getItems().get(0).getQuantity()).isEqualTo(2);

        // Assert – stock decreases with 2
        ArgumentCaptor<StockUpdateRequest> stockCaptor =
                ArgumentCaptor.forClass(StockUpdateRequest.class);
        verify(productServiceClient).updateStock(eq(1L), stockCaptor.capture());
        assertThat(stockCaptor.getValue().getQuantityChange()).isEqualTo(-2);

        // Assert – order  is saved
        verify(orderRepository).save(any(Order.class));
    }


    // ==========================================================
    // HELPERS
    // ==========================================================

    private OrderRequest buildOrderRequest(Long productId, int quantity) {
        OrderItemRequest item = new OrderItemRequest();
        item.setProductId(productId);
        item.setQuantity(quantity);

        OrderRequest request = new OrderRequest();
        request.setCustomerId("cust-123");
        request.setItems(List.of(item));
        return request;
    }
}
