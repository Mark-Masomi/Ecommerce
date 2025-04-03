package com.ecommerce.product_service.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info=@Info(
        title = "Product Service API",
        version = "1.0",
        description = "Documentation for Product Service"
))
public class OpenApiConfig {


}
