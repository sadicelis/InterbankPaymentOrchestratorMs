package com.payments.orchestrator.domain.model.aggregate;

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
            String bankId) {

        this.id = id;
        this.reference = reference;
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
        this.amount = amount;
        this.bankId = bankId;
        this.status = TransactionStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Transaction create(String reference,
            String sourceAccount,
            String destinationAccount,
            BigDecimal amount,
            String bankId) {

        return new Transaction(
                UUID.randomUUID(),
                reference,
                sourceAccount,
                destinationAccount,
                amount,
                bankId);
    }

    public static Transaction restore(UUID id,
            String reference,
            String sourceAccount,
            String destinationAccount,
            BigDecimal amount,
            String bankId,
            TransactionStatus status,
            String errorMessage,
            LocalDateTime updatedAt) {

        Transaction transaction = new Transaction(
                id,
                reference,
                sourceAccount,
                destinationAccount,
                amount,
                bankId);

        transaction.status = status;
        transaction.errorMessage = errorMessage;
        transaction.updatedAt = updatedAt;

        return transaction;
    }

    public void markSuccessful() {
        this.status = TransactionStatus.SUCCESSFUL;
        this.updatedAt = LocalDateTime.now();
    }

    public void markFailed(String errorMessage) {
        this.status = TransactionStatus.FAILED;
        this.errorMessage = errorMessage;
        this.updatedAt = LocalDateTime.now();
    }

}