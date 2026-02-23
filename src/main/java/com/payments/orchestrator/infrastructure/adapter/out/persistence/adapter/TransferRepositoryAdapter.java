/*package com.payments.orchestrator.infrastructure.adapter.out.persistence.adapter;

import com.payments.orchestrator.domain.model.aggregate.Transaction;
import com.payments.orchestrator.domain.model.enums.TransactionStatus;
import com.payments.orchestrator.domain.port.TransferRepositoryPort;
import com.payments.orchestrator.infrastructure.adapter.out.persistence.TransactionRepository;
import com.payments.orchestrator.infrastructure.adapter.out.persistence.BankJpaRepository;
import com.payments.orchestrator.infrastructure.adapter.out.persistence.Entities.TransactionEntity;
import com.payments.orchestrator.infrastructure.adapter.out.persistence.Entities.BankEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TransferRepositoryAdapter implements TransferRepositoryPort {

    /*private final TransactionRepository repository;
    private final BankJpaRepository bankJpaRepository;

    @Override
    public Transaction save(Transaction transaction) {

        TransactionEntity entity = toEntity(transaction);

        TransactionEntity saved = repository.save(entity);

        return toDomain(saved);
    }

    @Override
    public Optional<Transaction> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    // -------------------------
    // Manual mapping
    // -------------------------
/*
    private TransactionEntity toEntity(Transaction transaction) {

        BankEntity bank = bankJpaRepository.findById(transaction.getBankId())
                .orElseThrow(() -> new IllegalArgumentException("Bank not found"));

        return TransactionEntity.builder()
                .id(transaction.getId())
                .reference(transaction.getReference())
                .sourceAccount(transaction.getSourceAccount())
                .destinationAccount(transaction.getDestinationAccount())
                .amount(transaction.getAmount())
                .status(transaction.getStatus().getStatusMessage())
                .errorMessage(transaction.getErrorMessage())
                .bank(bank)
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }

    private Transaction toDomain(TransactionEntity entity) {

    TransactionStatus status = TransactionStatus.fromMessage(entity.getStatus());

    return Transaction.restore(
            entity.getId(),
            entity.getReference(),
            entity.getSourceAccount(),
            entity.getDestinationAccount(),
            entity.getAmount(),
            entity.getBank().getId(),
            status,
            entity.getErrorMessage(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
    );
}*/