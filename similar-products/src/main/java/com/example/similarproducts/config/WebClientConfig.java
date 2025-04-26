package com.example.similarproducts.config;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.core.IntervalFunction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

@Configuration
public class WebClientConfig {

    @Value("${mocks.base-url}")
    private String mocksBaseUrl;

    @Value("${webclient.timeout.connection:5000}")
    private int connectionTimeout;

    @Value("${webclient.timeout.read:5000}")
    private int readTimeout;

    @Value("${webclient.timeout.write:5000}")
    private int writeTimeout;

    @Value("${webclient.retry.max-attempts:3}")
    private int maxAttempts;

    @Value("${webclient.retry.initial-interval:1000}")
    private int initialInterval;

    @Value("${webclient.retry.max-interval:5000}")
    private int maxInterval;

    @Value("${webclient.retry.multiplier:2}")
    private int multiplier;

    @Bean
    public WebClient webClient() {
        ConnectionProvider provider = ConnectionProvider.builder("custom")
                .maxConnections(500)
                .maxIdleTime(Duration.ofMillis(connectionTimeout))
                .maxLifeTime(Duration.ofMillis(connectionTimeout))
                .build();

        HttpClient httpClient = HttpClient.create(provider)
                .responseTimeout(Duration.ofMillis(readTimeout))
                .option(io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout);

        return WebClient.builder()
                .baseUrl(mocksBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    @Bean
    public Retry retry() {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(maxAttempts)
                .intervalFunction(IntervalFunction.ofExponentialBackoff(
                        Duration.ofMillis(initialInterval),
                        multiplier,
                        Duration.ofMillis(maxInterval)))
                .retryOnException(throwable -> throwable instanceof WebClientResponseException &&
                        ((WebClientResponseException) throwable).getStatusCode().is5xxServerError())
                .build();

        return Retry.of("externalService", config);
    }
} 