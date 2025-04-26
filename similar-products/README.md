# Similar Products API

Aplicación backend para la prueba técnica de AMS Solutions.

## Descripción

La aplicación expone una API REST que permite obtener los detalles de productos similares a un producto dado. Para esto, consume dos APIs existentes:

1. `/product/{productId}/similarids`: Obtiene los IDs de productos similares
2. `/product/{productId}`: Obtiene los detalles de un producto específico

## Tecnologías Utilizadas

- Spring Boot 3.4.5
- Spring WebFlux
- Lombok
- Maven

## Estructura del Proyecto

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
│   │   │               └── config/
│   │   │                   └── WebClientConfig.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
└── pom.xml
```

## Endpoints

### GET /product/{productId}/similar

Obtiene los detalles de los productos similares a un producto dado.

**Parámetros**:

- `productId` (path): ID del producto

**Respuesta**:

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

**Ejemplo de respuesta**:

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
  },
  {
    "id": "4",
    "name": "Boots",
    "price": 39.99,
    "availability": true
  }
]
```

## Configuración

La aplicación está configurada para:

- Escuchar en el puerto 5001 (configurable en `application.properties`)
- Consumir los mocks en `http://localhost:3001`

## Ejecución

1. Iniciar los mocks:

```bash
docker-compose up -d simulado influxdb grafana
```

2. Ejecutar la aplicación:

```bash
./mvnw spring-boot:run
```

3. Probar el endpoint:

```bash
curl http://localhost:5001/product/1/similar
```
