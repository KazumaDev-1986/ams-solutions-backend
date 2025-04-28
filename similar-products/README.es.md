# API de Productos Similares

Aplicación backend para la prueba técnica de AMS Solutions.

## Descripción

La aplicación expone una API REST que permite obtener los detalles de productos similares a un producto dado. Para esto, consume dos APIs existentes:

1. `/product/{productId}/similarids`: Obtiene los IDs de productos similares
2. `/product/{productId}`: Obtiene los detalles de un producto específico

## Requisitos Previos

- Java 17 o superior
- Maven 3.6 o superior
- Docker y Docker Compose
- Git

## Tecnologías Utilizadas

- Spring Boot 3.4.5
- Spring WebFlux
- Lombok
- Maven
- Resilience4j para manejo de errores y retry
- JUnit 5 para pruebas
- Mockito para mocks en pruebas

## Instalación

1. Clonar el repositorio:

```bash
git clone <repository-url>
cd similar-products
```

2. Compilar el proyecto:

```bash
./mvnw clean install
```

## Ejecución

1. Iniciar los servicios de infraestructura:

```bash
docker-compose up -d simulado influxdb grafana
```

2. Verificar que los mocks estén funcionando:

```bash
curl http://localhost:3001/product/1/similarids
```

3. Ejecutar la aplicación:

```bash
./mvnw spring-boot:run
```

4. Probar el endpoint:

```bash
curl http://localhost:5000/product/1/similar
```

## Pruebas

### Pruebas Unitarias

```bash
./mvnw test
```

### Pruebas de Rendimiento

```bash
docker-compose run --rm k6 run scripts/test.js
```

### Verificar Resultados de Rendimiento

Abrir en el navegador: http://localhost:3000/d/Le2Ku9NMk/k6-performance-test

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

## Documentación de la API

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

**Códigos de Respuesta**:

- 200: OK - Lista de productos similares
- 404: Not Found - Producto no encontrado
- 500: Internal Server Error - Error en el servicio externo

**Ejemplo de respuesta exitosa**:

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

**Ejemplo de respuesta de error**:

```json
{
  "error": "Product not found with id: 1"
}
```

## Configuración

La aplicación está configurada para:

- Escuchar en el puerto 5000 (configurable en `application.properties`)
- Consumir los mocks en `http://localhost:3001`
- Timeouts configurables para las llamadas a servicios externos
- Política de retry para manejo de errores temporales

## Monitoreo

- Grafana: http://localhost:3000
- InfluxDB: http://localhost:8086

## Contribución

1. Fork el repositorio
2. Crear una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abrir un Pull Request
