# Similar Products API

Backend application for AMS Solutions technical test.

## Description

The application exposes a REST API that allows obtaining details of products similar to a given product. For this, it consumes two existing APIs:

1. `/product/{productId}/similarids`: Gets the IDs of similar products
2. `/product/{productId}`: Gets the details of a specific product

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Docker and Docker Compose
- Git

## Technologies Used

- Spring Boot 3.4.5
- Spring WebFlux
- Lombok
- Maven
- Resilience4j for error handling and retry
- JUnit 5 for testing
- Mockito for test mocks

## Installation

1. Clone the repository:

```bash
git clone <repository-url>
cd similar-products
```

2. Build the project:

```bash
./mvnw clean install
```

## Execution

1. Start infrastructure services:

```bash
docker-compose up -d simulado influxdb grafana
```

2. Verify that mocks are working:

```bash
curl http://localhost:3001/product/1/similarids
```

3. Run the application:

```bash
./mvnw spring-boot:run
```

4. Test the endpoint:

```bash
curl http://localhost:5000/product/1/similar
```

## Testing

### Unit Tests

```bash
./mvnw test
```

### Performance Tests

```bash
docker-compose run --rm k6 run scripts/test.js
```

### Check Performance Results

Open in browser: http://localhost:3000/d/Le2Ku9NMk/k6-performance-test

## Project Structure

```
similar-products/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── similarproducts/
│   │   │               ├── SimilarProductsApplication.java
│   │   │               ├── controller/
│   │   │               │   └── SimilarProductsController.java
│   │   │               ├── service/
│   │   │               │   └── SimilarProductsService.java
│   │   │               ├── model/
│   │   │               │   └── ProductDetail.java
│   │   │               ├── config/
│   │   │               │   └── WebClientConfig.java
│   │   │               └── exception/
│   │   │                   ├── ExternalServiceException.java
│   │   │                   └── ProductNotFoundException.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/
│               └── example/
│                   └── similarproducts/
│                       ├── controller/
│                       │   └── SimilarProductsControllerTest.java
│                       └── service/
│                           └── SimilarProductsServiceTest.java
└── pom.xml
```

## API Documentation

### GET /product/{productId}/similar

Gets the details of products similar to a given product.

**Parameters**:

- `productId` (path): Product ID

**Response**:

```json
[
  {
    "id": "string",
    "name": "string",
    "price": number,
    "availability": boolean
  }
]
```

**Response Codes**:

- 200: OK - List of similar products
- 404: Not Found - Product not found
- 500: Internal Server Error - External service error

**Successful response example**:

```json
[
  {
    "id": "2",
    "name": "Dress",
    "price": 19.99,
    "availability": true
  },
  {
    "id": "3",
    "name": "Blazer",
    "price": 29.99,
    "availability": false
  }
]
```

**Error response example**:

```json
{
  "error": "Product not found with id: 1"
}
```

## Configuration

The application is configured to:

- Listen on port 5000 (configurable in `application.properties`)
- Consume mocks at `http://localhost:3001`
- Configurable timeouts for external service calls
- Retry policy for handling temporary errors

## Monitoring

- Grafana: http://localhost:3000
- InfluxDB: http://localhost:8086

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request
