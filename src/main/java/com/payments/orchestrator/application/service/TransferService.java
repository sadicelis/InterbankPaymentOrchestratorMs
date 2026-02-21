
package com.payments.orchestrator.application.service;

import com.payments.orchestrator.domain.model.TransactionStatus;
import com.payments.orchestrator.infrastructure.adapter.in.rest.TransferController.TransferRequest;
import com.payments.orchestrator.infrastructure.adapter.out.persistence.BankEntity;
import com.payments.orchestrator.infrastructure.adapter.out.persistence.TransactionEntity;
import com.payments.orchestrator.infrastructure.adapter.out.persistence.TransactionRepository;

import jakarta.transaction.Transactional;

import com.payments.orchestrator.infrastructure.adapter.out.persistence.BankJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TransferService {

    private final TransactionRepository repository;
    private final BankJpaRepository bankJpaRepository;

    public UUID process(TransferRequest request) {

    BankEntity bank = bankJpaRepository
            .findByCodeAndActiveTrue(request.getBankCode())
            .orElseThrow(RuntimeException ::new);

    TransactionEntity transaction = TransactionEntity.builder()
            .id(UUID.randomUUID())
            .reference(request.getReference())
            .sourceAccount(request.getSourceAccount())
            .destinationAccount(request.getDestinationAccount())
            .amount(request.getAmount())
            .status(TransactionStatus.PENDING.getStatusMessage())
            .bank(bank) 
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    repository.save(transaction);

    return transaction.getId();
}
}
