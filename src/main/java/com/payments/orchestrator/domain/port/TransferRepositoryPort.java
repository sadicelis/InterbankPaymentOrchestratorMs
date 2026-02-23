package com.payments.orchestrator.domain.port;

import com.payments.orchestrator.domain.model.aggregate.Transaction;
import com.payments.orchestrator.infrastructure.adapter.in.rest.dto.TransferRequestDto;

import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

public interface TransferRepositoryPort {

    Mono<Transaction> savePending(Transaction request);

    //Optional<Transaction> findById(UUID id);
}