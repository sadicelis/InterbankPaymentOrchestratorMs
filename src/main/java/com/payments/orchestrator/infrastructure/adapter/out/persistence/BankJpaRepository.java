
package com.payments.orchestrator.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payments.orchestrator.infrastructure.adapter.out.persistence.Entities.BankEntity;

import java.util.Optional;
import java.util.UUID;

public interface BankJpaRepository extends JpaRepository<BankEntity, UUID> {

    Optional<BankEntity> findByCodeAndActiveTrue(String code);
}