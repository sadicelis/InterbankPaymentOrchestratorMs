
package com.payments.orchestrator.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payments.orchestrator.infrastructure.adapter.out.persistence.Entities.TransactionEntity;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {
}
