package com.ecommerce.order_service.controller;

import com.ecommerce.order_service.dto.OrderItemResponse;
import com.ecommerce.order_service.dto.OrderResponse;
import com.ecommerce.order_service.model.OrderStatus;
import com.ecommerce.order_service.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    // ==========================================================
    // HAPPY PATH
    // ==========================================================

    @Test
    @DisplayName("POST /api/orders: should return 201 with OrderResponse")
    void createOrder_shouldReturn201()throws Exception{
        OrderResponse response = OrderResponse.builder()
                .orderNumber("abc-123")
                .orderDate(LocalDateTime.now())
                .totalPrice(new BigDecimal("25980.00"))
                .status(OrderStatus.PENDING)
                .items(List.of(
                        OrderItemResponse.builder()
                                .productId(1L)
                                .sku("IPHONE15-001")
                                .quantity(2)
                                .unitPrice(new BigDecimal("12990.00"))
                                .build()
                ))
                .build();

        when(orderService.createOrder(any())).thenReturn(response);

        String body = objectMapper.writeValueAsString(Map.of(

                "customerId" , "cust-123",
                "items", List.of(Map.of("productId",1, "quantity", 2))
        ));
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderNumber").value("abc-123"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.totalPrice").value(25980.00))
                .andExpect(jsonPath("$.items[0].sku").value("IPHONE15-001"))
                .andExpect(jsonPath("$.items[0].quantity").value(2));
    }

    // ==========================================================
    // VALIDATION
    // ==========================================================

    @Test
    @DisplayName("POST /api/orders: should return 400 when customerId is missing")
    void createOrder_shouldReturn400_whenCustomerIdMissing()throws Exception{

        String body = objectMapper.writeValueAsString(Map.of(
                "items", List.of(Map.of("productId" , 1, "quantity" , 2))
        ));

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString
                        ("customerId")));
    }



}
