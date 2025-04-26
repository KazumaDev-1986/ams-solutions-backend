package com.example.similarproducts.service;

import com.example.similarproducts.exception.ExternalServiceException;
import com.example.similarproducts.exception.ProductNotFoundException;
import com.example.similarproducts.model.ProductDetail;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.reactor.retry.RetryOperator;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class SimilarProductsService {

    private final WebClient webClient;
    private final Retry retry;

    public SimilarProductsService(WebClient webClient, Retry retry) {
        this.webClient = webClient;
        this.retry = retry;
    }

    public Mono<List<ProductDetail>> getSimilarProducts(String productId) {
        return getSimilarProductIds(productId)
                .flatMapMany(ids -> Mono.just(ids).flatMapIterable(list -> list))
                .flatMap(this::getProductDetail)
                .collectList();
    }

    private Mono<List<String>> getSimilarProductIds(String productId) {
        return webClient.get()
                .uri("/product/{productId}/similarids", productId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                .onErrorMap(WebClientResponseException.class, ex -> {
                    if (ex.getStatusCode().value() == 404) {
                        return new ProductNotFoundException(productId);
                    }
                    return new ExternalServiceException("similarids", ex.getMessage());
                })
                .transform(RetryOperator.of(retry));
    }

    private Mono<ProductDetail> getProductDetail(String productId) {
        return webClient.get()
                .uri("/product/{productId}", productId)
                .retrieve()
                .bodyToMono(ProductDetail.class)
                .onErrorMap(WebClientResponseException.class, ex -> {
                    if (ex.getStatusCode().value() == 404) {
                        return new ProductNotFoundException(productId);
                    }
                    return new ExternalServiceException("product", ex.getMessage());
                })
                .transform(RetryOperator.of(retry));
    }
} 