package com.example.similarproducts.service;

import com.example.similarproducts.exception.ExternalServiceException;
import com.example.similarproducts.exception.ProductNotFoundException;
import com.example.similarproducts.model.ProductDetail;
import io.github.resilience4j.retry.Retry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimilarProductsServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private SimilarProductsService service;

    @BeforeEach
    void setUp() {
        Retry retry = Retry.ofDefaults("test");
        service = new SimilarProductsService(webClient, retry);
        
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(String.class), any(Object[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    void getSimilarProducts_Success() {
        // Arrange
        String productId = "1";
        List<String> similarIds = Arrays.asList("2", "3");
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

        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(similarIds));
        when(responseSpec.bodyToMono(ProductDetail.class))
                .thenReturn(Mono.just(product1))
                .thenReturn(Mono.just(product2));

        // Act & Assert
        StepVerifier.create(service.getSimilarProducts(productId))
                .expectNext(Arrays.asList(product1, product2))
                .verifyComplete();
    }

    @Test
    void getSimilarProducts_EmptyList() {
        // Arrange
        String productId = "1";
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(Collections.emptyList()));

        // Act & Assert
        StepVerifier.create(service.getSimilarProducts(productId))
                .expectNext(Collections.emptyList())
                .verifyComplete();
    }

    @Test
    void getSimilarProducts_ProductNotFound() {
        // Arrange
        String productId = "1";
        WebClientResponseException notFoundException = WebClientResponseException.create(
                404, "Not Found", null, null, null);

        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.error(notFoundException));

        // Act & Assert
        StepVerifier.create(service.getSimilarProducts(productId))
                .expectError(ProductNotFoundException.class)
                .verify();
    }

    @Test
    void getSimilarProducts_ExternalServiceError() {
        // Arrange
        String productId = "1";
        WebClientResponseException serverError = WebClientResponseException.create(
                500, "Internal Server Error", null, null, null);

        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.error(serverError));

        // Act & Assert
        StepVerifier.create(service.getSimilarProducts(productId))
                .expectError(ExternalServiceException.class)
                .verify();
    }
} 