package com.example.similarproducts.service;

import com.example.similarproducts.model.ProductDetail;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class SimilarProductsService {

    private final WebClient webClient;

    public SimilarProductsService(WebClient webClient) {
        this.webClient = webClient;
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
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {});
    }

    private Mono<ProductDetail> getProductDetail(String productId) {
        return webClient.get()
                .uri("/product/{productId}", productId)
                .retrieve()
                .bodyToMono(ProductDetail.class);
    }
} 