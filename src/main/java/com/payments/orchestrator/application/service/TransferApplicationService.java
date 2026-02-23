package com.payments.orchestrator.application.service;

import com.payments.orchestrator.domain.model.aggregate.Transaction;
import com.payments.orchestrator.infrastructure.adapter.out.persistence.adapter.TransactionRepositoryAdapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Service;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferApplicationService {

    private final BankStrategyResolver strategyResolver;
    private final TransactionRepositoryAdapter repository;

    @CircuitBreaker(name = "bankService")
    @Retry(name = "bankService")
    public Mono<UUID> process(Transaction transaction) {

        log.info("Starting transaction {}", transaction.getId());

        return repository.savePending(transaction)
                .flatMap(saved -> {

                    var strategy = strategyResolver.resolve(saved.getBankId());

                    return strategy.sendTransfer(saved)
                            .flatMap(success -> {

                                if (Boolean.TRUE.equals(success)) {
                                    saved.markSuccessful();
                                } else {
                                    saved.markFailed("Bank returned failure");
                                }

                                return repository.updateStatus(saved);
                            });
                })
                .map(Transaction::getId)
                .doOnSuccess(id -> log.info("Transaction completed {}", id))
                .doOnError(error -> log.error("Transaction failed", error));
    }
}