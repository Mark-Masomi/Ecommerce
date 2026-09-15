# E-commerce Microservices API

A backend e-commerce system built with Spring Boot microservices architecture using Java and Gradle.

## Features

- Service Discovery with Netflix Eureka
- API Gateway with Spring Cloud Gateway
- RESTful APIs for product, order, user, and inventory management
- H2 in-memory database for development
- Gradle multi-project build setup
- Docker containerization support

## Technologies

- Java 17
- Kotlin 1.9+
- Spring Boot 3.2.x
- Spring Cloud 2023.0.x
- Spring Data JPA
- H2 Database
- Gradle 8.x
- Docker

## Services Overview

| Service              | Port  | Description                           |
|----------------------|-------|---------------------------------------|
| Service Registry     | 8761  | Netflix Eureka service discovery      |
| API Gateway          | 8080  | Route and filter requests             |
| Product Service      | 8081  | Product catalog management            |
| Order Service        | 8082  | Order processing system               |
| User Service         | 8083  | User authentication & management      |
| Inventory Service    | 8084  | Stock management system               |

## Getting Started

### Prerequisites

- Java 17 JDK
- Gradle 8.x
- Docker (optional)

### Installation

1. Clone the repository:
    ```bash
    git clone https://github.com/Mark-Masomi/Ecommerce.git
    cd Ecommerce
    ```

2. Build all services:
    ```bash
    ./gradlew build
    ```

### Running the System

1. Start the Service Registry:
    ```bash
    cd service-registry
    ./gradlew bootRun
    ```

2. Start the API Gateway:
    ```bash
    cd api-gateway
    ./gradlew bootRun
    ```

3. Start other services in separate terminals:

    **Product Service**
    ```bash
    cd product-service
    ./gradlew bootRun
    ```

    **Order Service**
    ```bash
    cd order-service
    ./gradlew bootRun
    ```

    **Repeat for other services.**

### API Documentation

Access Swagger UI for individual services:

- Product Service: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
- Order Service: [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html)
- API Gateway: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### Deployment with Docker

1. Build Docker images for each service:
    ```bash
    ./gradlew bootBuildImage
    ```

2. Run containers using Docker Compose:
    ```bash
    docker-compose up -d
    ```

### Configuration

Service configurations are located in each service's `src/main/resources/application.yml`.

### Monitoring

Access Eureka Dashboard:  
[http://localhost:8761](http://localhost:8761)

### Example API Calls

**Create a product:**
```bash
curl -X POST -H "Content-Type: application/json" \
-d '{"name": "Laptop", "price": 999.99, "description": "High-performance laptop"}' \
http://localhost:8080/api/products
```

**Get all products:**
```bash
curl http://localhost:8080/api/products
```

## Future Enhancements

- Add Spring Cloud Config for centralized configuration
- Implement Circuit Breaker pattern with Resilience4j
- Add OpenAPI documentation
- Implement JWT-based authentication
- Add Kubernetes deployment manifests

## Contributing

Contributions are welcome with prior written permission from the copyright holder.

If you would like to contribute, please contact the repository owner before making changes or submitting a pull request.

## License

Copyright © 2025 Mark-Masomi. All rights reserved.

This project is publicly available for viewing and educational reference. No permission is granted to use, copy, modify, distribute, or commercially use this source code without prior written permission.

See [LICENSE](LICENSE) for the full license terms.
