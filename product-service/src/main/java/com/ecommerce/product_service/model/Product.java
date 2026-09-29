package com.ecommerce.product_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name="products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name="stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Column(name="is_available",nullable = false)
    private Boolean available;

    @Version
    @Column(nullable = false)
    private Long version;


}
