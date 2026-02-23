package com.payments.orchestrator.infrastructure.adapter.out.persistence.mapper;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.payments.orchestrator.domain.model.aggregate.Transaction;
import com.payments.orchestrator.domain.model.enums.TransactionStatus;
import com.payments.orchestrator.infrastructure.adapter.out.persistence.Entities.TransactionEntity;
import com.payments.orchestrator.infrastructure.adapter.out.persistence.Entities.BankEntity;

@Component
public class TransactionMapper {

    public static Transaction toDomain(TransactionEntity entity) {
        if (entity == null) {
            return null;
        }

        return Transaction.restore(
                entity.getId(),
                entity.getReference(),
                entity.getSourceAccount(),
                entity.getDestinationAccount(),
                entity.getAmount(),
                entity.getBank() != null ? entity.getBank().getCode() : null,
                TransactionStatus.fromMessage(entity.getStatus()),
                entity.getErrorMessage(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
                
        );
    }

    public static TransactionEntity toEntity(Transaction domain, BankEntity bankEntity) {
        if (domain == null) {
            return null;
        }

        return TransactionEntity.builder()
                .id(domain.getId())
                .reference(domain.getReference())
                .sourceAccount(domain.getSourceAccount())
                .destinationAccount(domain.getDestinationAccount())
                .amount(domain.getAmount())
                .status(domain.getStatus().getStatusMessage())
                .bank(bankEntity)
                .errorMessage(domain.getErrorMessage())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}