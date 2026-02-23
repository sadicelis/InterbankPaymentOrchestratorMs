package com.payments.orchestrator.application.service;

import com.payments.orchestrator.domain.model.BankTransferResult;
import com.payments.orchestrator.domain.model.aggregate.Transaction;
import com.payments.orchestrator.domain.port.TransferRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Service;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferApplicationService {

    private final BankStrategyResolver strategyResolver;
    private final TransferRepositoryPort repository; 

    @CircuitBreaker(name = "bankService")
    @Retry(name = "bankService")
    public Mono<UUID> process(Transaction transaction) {

        log.info("Starting transaction {}", transaction.getId());

        return repository.savePending(transaction)
                .flatMap(saved -> {
                    var strategy = strategyResolver.resolve(saved.getBankId());

                    return strategy.sendTransfer(saved)
                            .timeout(Duration.ofSeconds(3))
                            .flatMap(result -> finalizeAndPersist(saved, result))
                            .onErrorResume(ex -> {
                                saved.markFailed(normalizeError(ex));
                                return repository.updateStatus(saved);
                            });
                })
                .map(Transaction::getId)
                .doOnSuccess(id -> log.info("Transaction completed {}", id))
                .doOnError(error -> log.error("Transaction failed", error));
    }

    private Mono<Transaction> finalizeAndPersist(Transaction saved, BankTransferResult result) {
        if (result.isSuccess()) {
            saved.markSuccessful();
        } else {
            saved.markFailed(buildFailureMessage(result));
        }
        return repository.updateStatus(saved);
    }

    private String buildFailureMessage(BankTransferResult result) {
        String code = result.getCode() == null ? "UNKNOWN" : result.getCode();
        String msg = result.getMessage() == null ? "Bank returned failure" : result.getMessage();
        return code + " - " + msg;
    }

    private String normalizeError(Throwable ex) {
        return ex.getClass().getSimpleName() + ": " + (ex.getMessage() == null ? "N/A" : ex.getMessage());
    }
}