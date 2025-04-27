package com.example.similarproducts.controller;

import com.example.similarproducts.model.ProductDetail;
import com.example.similarproducts.service.SimilarProductsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(SimilarProductsController.class)
class SimilarProductsControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private SimilarProductsService similarProductsService;

    @Test
    void getSimilarProducts_Success() {
        // Arrange
        String productId = "1";
        ProductDetail product1 = new ProductDetail();
        product1.setId("2");
        product1.setName("Product 2");
        product1.setPrice(100.0);
        product1.setAvailability(true);

        ProductDetail product2 = new ProductDetail();
        product2.setId("3");
        product2.setName("Product 3");
        product2.setPrice(200.0);
        product2.setAvailability(false);

        List<ProductDetail> expectedProducts = Arrays.asList(product1, product2);
        when(similarProductsService.getSimilarProducts(productId))
                .thenReturn(Mono.just(expectedProducts));

        // Act & Assert
        webTestClient.get()
                .uri("/product/{productId}/similar", productId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductDetail.class)
                .hasSize(2)
                .contains(product1, product2);
    }

    @Test
    void getSimilarProducts_EmptyList() {
        // Arrange
        String productId = "1";
        when(similarProductsService.getSimilarProducts(productId))
                .thenReturn(Mono.just(Collections.emptyList()));

        // Act & Assert
        webTestClient.get()
                .uri("/product/{productId}/similar", productId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductDetail.class)
                .hasSize(0);
    }

    @Test
    void getSimilarProducts_ProductNotFound() {
        // Arrange
        String productId = "1";
        when(similarProductsService.getSimilarProducts(productId))
                .thenReturn(Mono.error(new RuntimeException("Product not found")));

        // Act & Assert
        webTestClient.get()
                .uri("/product/{productId}/similar", productId)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void getSimilarProducts_InvalidProductId() {
        // Act & Assert
        webTestClient.get()
                .uri("/product/{productId}/similar", "")
                .exchange()
                .expectStatus().isNotFound();
    }
} 