package com.ecommerce.order_service.service;
import com.ecommerce.order_service.dto.*;
import com.ecommerce.order_service.exeption.InsufficientStockException;
import com.ecommerce.order_service.feign.ProductServiceClient;
import com.ecommerce.order_service.model.Order;
import com.ecommerce.order_service.model.OrderItem;
import com.ecommerce.order_service.model.OrderStatus;
import com.ecommerce.order_service.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductServiceClient productServiceClient;


    @Override
    @Transactional
    @CircuitBreaker(name = "orderService", fallbackMethod = "fallbackCreateOrder")
    public OrderResponse createOrder(OrderRequest orderRequest) {

       validateStock(orderRequest.getItems());

       Order order = mapToOrder(orderRequest);
       Order saveOrder = orderRepository.save(order);

       updateProductStock(orderRequest.getItems());




        return null;
    }

    private void validateStock(List<OrderItemRequest> items){
        for(OrderItemRequest item: items){

            Integer availableStock= productServiceClient.getProductStock(item.getProductId());
            if (availableStock == null || availableStock < item.getQuantity()){

                throw new InsufficientStockException("Insufficient stock for product ID: "+ item.getProductId()+
                        ". Available: " + availableStock+", requsted: "+ item.getQuantity()
                );
            }

        }
    }


    private void updateProductStock(List<OrderItemRequest> items){
        for (OrderItemRequest item:items) {

            productServiceClient.updateStock(
                    item.getProductId(), new StockUpdateRequest(-item.getQuantity())
            );

        }
    }

    //
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

     //
    // Mappa listan av OrderItemRequest -> OrderItem (embeddable)
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
