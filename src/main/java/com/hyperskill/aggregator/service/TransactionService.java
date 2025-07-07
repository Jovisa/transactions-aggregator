package com.hyperskill.aggregator.service;

import com.hyperskill.aggregator.config.TransactionProperties;
import com.hyperskill.aggregator.dto.Transaction;
import com.hyperskill.aggregator.exception.ExternalServiceException;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.util.Comparator;
import java.util.List;


@Service
@AllArgsConstructor
public class TransactionService {
    private final WebClient webClient;
    private final TransactionProperties transactionProperties;

    // caching implemented with async
    @Cacheable(cacheNames = "data", key = "#account")
    public Mono<List<Transaction>> aggregate(String account) {

        Flux<Transaction> flux1 = fetchTransactionsWithRetry(transactionProperties.getHost1(), account);
        Flux<Transaction> flux2 = fetchTransactionsWithRetry(transactionProperties.getHost2(), account);

        return Flux.merge(flux1, flux2)
                .collectSortedList(Comparator.comparing(Transaction::timestamp).reversed());
    }


    // async implemented
    public Flux<Transaction> fetchTransactionsWithRetry(String hostServerUrl, String account) {
        String url = String.format("%s/transactions?account=%s", hostServerUrl, account);

        return webClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        Mono.error(new ExternalServiceException(response.statusCode()))
                )
                .bodyToFlux(Transaction.class)
                .retryWhen(Retry.max(transactionProperties.getMaxRetries())
                        .filter(throwable -> throwable instanceof ExternalServiceException))
                .onErrorResume(e -> {
                    System.out.println("Error fetching from " + hostServerUrl + ": " + e.getMessage());
                    return Flux.empty();
                });

    }

}
