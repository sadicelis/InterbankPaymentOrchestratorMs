package com.payments.orchestrator.application.service;

import com.payments.orchestrator.domain.model.BankTransferResult;
import com.payments.orchestrator.domain.model.aggregate.Transaction;
import com.payments.orchestrator.domain.port.TransferRepositoryPort;
import com.payments.orchestrator.application.exception.ApplicationException;
import com.payments.orchestrator.infrastructure.exception.InfrastructureException;
import com.payments.orchestrator.domain.exception.DomainException;
import com.payments.orchestrator.application.exception.TransferProcessingException;
import com.payments.orchestrator.infrastructure.config.constants.BankResponseConstants;
import com.payments.orchestrator.infrastructure.config.constants.DefaultValuesConstants;
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

    @CircuitBreaker(name = "bankService", fallbackMethod = "handleCircuitBreakerFallback")
    @Retry(name = "bankService")
    public Mono<UUID> process(Transaction transaction) {

        log.info("Starting transaction processing: txnId={}", transaction.getId());

        return repository.savePending(transaction)
            .flatMap(saved -> {
                try {
                    var strategy = strategyResolver.resolve(saved.getBankId());
                    return strategy.sendTransfer(saved)
                        .timeout(Duration.ofSeconds(BankResponseConstants.BANK_REQUEST_TIMEOUT_SECONDS))
                        .flatMap(result -> {
                            if (result.isSuccess()) {
                                saved.markSuccessful();
                                log.info("Transaction successful: txnId={}, externalRef={}", 
                                        saved.getId(), result.getExternalRef());
                            } else {
                                String errorMsg = buildFailureMessage(result);
                                saved.markFailed(errorMsg);
                                log.warn("Transaction failed: txnId={}, error={}", saved.getId(), errorMsg);
                            }
                            return repository.updateStatus(saved);
                        })
                        .onErrorResume(ex -> {
                            String errorMsg = normalizeError(ex);
                            saved.markFailed(errorMsg);
                            log.error("Error processing transfer: txnId={}, error={}", saved.getId(), errorMsg, ex);
                            return repository.updateStatus(saved);
                        });
                } catch (Exception ex) {
                    String errorMsg = normalizeError(ex);
                    saved.markFailed(errorMsg);
                    log.error("Error resolving bank strategy: txnId={}, bankId={}, error={}", 
                            saved.getId(), saved.getBankId(), errorMsg, ex);
                    return repository.updateStatus(saved)
                        .then(Mono.error(ex));
                }
            })
            .map(Transaction::getId)
            .onErrorMap(ex -> {
                if (ex instanceof ApplicationException 
                    || ex instanceof InfrastructureException
                    || ex instanceof DomainException) {
                    return ex;
                }
                return new TransferProcessingException(
                        "Unexpected error: " + ex.getMessage(), ex);
            });
    }

    public Mono<UUID> handleCircuitBreakerFallback(Transaction transaction, Exception ex) {
        log.error("Circuit breaker open for transaction: txnId={}", transaction.getId(), ex);
        return Mono.error(new TransferProcessingException(
                "Bank service temporarily unavailable due to repeated failures", ex
        ));
    }

    private String buildFailureMessage(BankTransferResult result) {
        String code = result.getCode() == null ? DefaultValuesConstants.DEFAULT_UNKNOWN : result.getCode();
        String msg = result.getMessage() == null ? DefaultValuesConstants.DEFAULT_BANK_ERROR_MESSAGE : result.getMessage();
        return code + " - " + msg;
    }

    private String normalizeError(Throwable ex) {
        return ex.getClass().getSimpleName() + ": " + (ex.getMessage() == null ? DefaultValuesConstants.DEFAULT_NOT_AVAILABLE : ex.getMessage());
    }
}