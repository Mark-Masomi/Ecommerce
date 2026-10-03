package com.ecommerce.order_service.service;
import com.ecommerce.order_service.dto.*;
import com.ecommerce.order_service.exeption.InsufficientStockException;
import com.ecommerce.order_service.exeption.ServiceUnavailableException;
import com.ecommerce.order_service.feign.ProductServiceClient;
import com.ecommerce.order_service.model.Order;
import com.ecommerce.order_service.model.OrderItem;
import com.ecommerce.order_service.model.OrderStatus;
import com.ecommerce.order_service.repository.OrderRepository;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductServiceClient productServiceClient;


    @Override
    @Transactional
    @CircuitBreaker(name = "orderService", fallbackMethod = "fallbackCreateOrder")
    public OrderResponse createOrder(OrderRequest orderRequest) {

       validateStock(orderRequest.getItems());

       Order order = mapToOrder(orderRequest);
       Order savedOrder = orderRepository.save(order);

       updateProductStock(orderRequest.getItems());


        return mapToOrderResponse(savedOrder);
    }

    // ==========================================================
    // STOCK VALIDATION
    // ==========================================================

    private void validateStock(List<OrderItemRequest> items){
        for(OrderItemRequest item: items){

            Integer availableStock= productServiceClient.getProductStock(item.getProductId());
            if (availableStock == null || availableStock < item.getQuantity()){

                throw new InsufficientStockException("Insufficient stock for product ID: "+ item.getProductId()+
                        ". Available: " + availableStock+
                        ", requsted: "+ item.getQuantity()
                );
            }

        }
    }

    // ==========================================================
    // STOCK UPDATE WITH RETRY
    // ==========================================================

    /**
     * Updates stock for all items.
     * If a 409 CONFLICT (optimistic locking in product-service) occurs,
     * Resilience4j automatically retries up to maxAttempts.
     * If all retries are exhausted, the original error is thrown
     * and caught by the Circuit Breaker.
     */
    @Retry(name = "stockUpdateRetry",fallbackMethod = "stockUpdateFallback")
    private void updateProductStock(List<OrderItemRequest> items){
        for (OrderItemRequest item:items) {
            try {

                productServiceClient.updateStock(
                        item.getProductId(), new StockUpdateRequest(-item.getQuantity())
                );
            } catch (FeignException.Conflict ex){
                log.warn("Conflict when updating stock for product {}: {}",
                        item.getProductId(),ex.getMessage());
                throw ex;
            }catch (FeignException ex){
                log.error("Failed to update stock for product {}: {}",
                        item.getProductId(),ex.getMessage());
                throw ex;
            }

        }
    }


    /**
     * Fallback when retries are exhausted.
     * Throw a clear error indicating that the order could not be completed
     * due to concurrent changes to the stock.
     */

    public void stockUpdateFallback(List<OrderItemRequest> items,Throwable t){
        log.error("Stock update failed after retries: {}", t.getMessage(),t);
        throw new ServiceUnavailableException(
                "Could not reserve stock due to concurrent updates. " +
                        "Please try again."
        );

    }



     private Order mapToOrder(OrderRequest request){
        return Order.builder()
                .orderNumber(UUID.randomUUID().toString())
                .orderDate(LocalDateTime.now())
                .items(mapToOrderItems(request.getItems()))
                .totalPrice(calculateTotalPrice(request.getItems()))
                .customerId(request.getCustomerId())
                .status(OrderStatus.PENDING)
                .build();
     }

    // ==========================================================
    // MAPPING
    // ==========================================================


    //
    // Map the list of OrderItemRequest -> OrderItem (embeddable)
    private List<OrderItem> mapToOrderItems(List<OrderItemRequest> items) {
        return items.stream()
                .map(item -> {
                    ProductDto product = productServiceClient.getProductById(item.getProductId());
                    return OrderItem.builder()
                            .productId(item.getProductId())
                            .sku(product.getSku())
                            .quantity(item.getQuantity())
                            .unitPrice(product.getPrice())
                            .build();
                })
                .collect(Collectors.toList());
    }

    //
    // Beräkna totalpris genom att hämta pris från product-service
    private BigDecimal calculateTotalPrice(List<OrderItemRequest> items) {
        return items.stream()
                .map(item -> {
                    ProductDto product = productServiceClient.getProductById(item.getProductId());
                    return product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private OrderResponse mapToOrderResponse(Order order) {
        return OrderResponse.builder()
                .orderNumber(order.getOrderNumber())
                .orderDate(order.getOrderDate())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .items(order.getItems().stream().map(this::mapToOrderItemResponse)
                        .collect(Collectors.toList()))
                .build();


    }

    private OrderItemResponse mapToOrderItemResponse(OrderItem item){
        return OrderItemResponse.builder()
                .productId(item.getProductId())
                .sku(item.getSku())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .build();
    }

    // fallback-method for circuit breaker
    public OrderResponse fallBackCreateOrder(OrderRequest orderRequest,Throwable t){
        throw new ServiceUnavailableException("Order service is temporarily unavailable, please try again later.");
    }


    //
    public void checkStock(Long productId,int requstedQuantity){
        ProductDto product=productServiceClient.getProductById(productId);
        if(product.getStockQuantity() < requstedQuantity){
            throw new InsufficientStockException("Only "+product.getStockQuantity() + " in stock");
        }
    }

    //
    public void reduceStock(Long productId, int quantity){
        productServiceClient.updateStock(productId,new StockUpdateRequest(-quantity));
    }
}
