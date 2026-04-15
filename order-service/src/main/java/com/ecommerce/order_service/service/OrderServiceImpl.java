package com.ecommerce.order_service.service;
import com.ecommerce.order_service.dto.*;
import com.ecommerce.order_service.exeption.InsufficientStockException;
import com.ecommerce.order_service.feign.ProductServiceClient;
import com.ecommerce.order_service.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductServiceClient productServiceClient;


    @Override
    @Transactional
    @CircuitBreaker(name = "orderService", fallbackMethod = "fallbackCreateOrder")
    public OrderResponse createOrder(OrderRequest orderRequest) {

       // validateStock(orderRequest.getItems());


        return null;
    }

//     private void validateStock(@NotEmpty(message = "Order must contain at least one item") @Valid List<OrderItemRequest> items){
//        items.forEach(item ->{
//            int avalableStock = productServiceClient.get
//        });
//     }

    public void checkStock(Long productId,int requstedQuantity){
        ProductDto product=productServiceClient.getProductById(productId);
        if(product.getStockQuantity() < requstedQuantity){
            throw new InsufficientStockException("Only "+product.getStockQuantity() + " in stock");
        }
    }

    public void reduceStock(Long productId, int quantity){
        productServiceClient.updateStock(productId,new StockUpdateRequest(-quantity));
    }
}
