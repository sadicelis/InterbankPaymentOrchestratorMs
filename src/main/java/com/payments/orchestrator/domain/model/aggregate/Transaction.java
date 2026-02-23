package com.payments.orchestrator.domain.model.aggregate;

import com.payments.orchestrator.domain.exception.InvalidTransactionException;
import com.payments.orchestrator.domain.model.enums.TransactionStatus;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Transaction {

    private final UUID id;
    private final String reference;
    private final String sourceAccount;
    private final String destinationAccount;
    private final BigDecimal amount;
    private TransactionStatus status;
    private final String bankId;
    private String errorMessage;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Transaction(UUID id,
                        String reference,
                        String sourceAccount,
                        String destinationAccount,
                        BigDecimal amount,
                        String bankId,
                        TransactionStatus status,
                        String errorMessage,
                        LocalDateTime createdAt,
                        LocalDateTime updatedAt) {

        this.id = id;
        this.reference = reference;
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
        this.amount = amount;
        this.bankId = bankId;

        this.status = status;
        this.errorMessage = errorMessage;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Transaction create(String reference,
                                     String sourceAccount,
                                     String destinationAccount,
                                     BigDecimal amount,
                                     String bankId) {

        // Validaciones de dominio
        if (reference == null || reference.isBlank()) {
            throw new InvalidTransactionException("Transaction reference cannot be null or empty");
        }

        if (sourceAccount == null || sourceAccount.isBlank()) {
            throw new InvalidTransactionException("Source account cannot be null or empty");
        }

        if (destinationAccount == null || destinationAccount.isBlank()) {
            throw new InvalidTransactionException("Destination account cannot be null or empty");
        }

        if (sourceAccount.equals(destinationAccount)) {
            throw new InvalidTransactionException("Source and destination accounts cannot be the same");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("Amount must be greater than zero");
        }

        if (bankId == null || bankId.isBlank()) {
            throw new InvalidTransactionException("Bank ID cannot be null or empty");
        }

        LocalDateTime now = LocalDateTime.now();
        return new Transaction(
                UUID.randomUUID(),
                reference,
                sourceAccount,
                destinationAccount,
                amount,
                bankId,
                TransactionStatus.PENDING,
                null,
                now,
                now
        );
    }

    public static Transaction restore(UUID id,
                                      String reference,
                                      String sourceAccount,
                                      String destinationAccount,
                                      BigDecimal amount,
                                      String bankId,
                                      TransactionStatus status,
                                      String errorMessage,
                                      LocalDateTime createdAt,
                                      LocalDateTime updatedAt) {

        return new Transaction(
                id,
                reference,
                sourceAccount,
                destinationAccount,
                amount,
                bankId,
                status,
                errorMessage,
                createdAt,
                updatedAt
        );
    }

    public void markSuccessful() {
        this.status = TransactionStatus.SUCCESSFUL;
        this.errorMessage = null;
        this.updatedAt = LocalDateTime.now();
    }

    public void markFailed(String errorMessage) {
        this.status = TransactionStatus.FAILED;
        this.errorMessage = errorMessage;
        this.updatedAt = LocalDateTime.now();
    }
}