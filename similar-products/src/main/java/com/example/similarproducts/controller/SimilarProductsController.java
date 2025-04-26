package com.example.similarproducts.controller;

import com.example.similarproducts.model.ProductDetail;
import com.example.similarproducts.service.SimilarProductsService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class SimilarProductsController {

    private final SimilarProductsService similarProductsService;

    public SimilarProductsController(SimilarProductsService similarProductsService) {
        this.similarProductsService = similarProductsService;
    }

    @GetMapping(value = "/product/{productId}/similar", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<ProductDetail>> getSimilarProducts(@PathVariable String productId) {
        return similarProductsService.getSimilarProducts(productId);
    }
} 