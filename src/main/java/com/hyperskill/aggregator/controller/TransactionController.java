package com.hyperskill.aggregator.controller;

import com.hyperskill.aggregator.dto.Transaction;
import com.hyperskill.aggregator.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@AllArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/aggregate")
    public Mono<List<Transaction>> aggregate(@RequestParam String account) {
        return transactionService.aggregate(account);
    }

}
