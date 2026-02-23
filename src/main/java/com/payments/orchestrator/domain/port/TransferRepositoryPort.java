package com.payments.orchestrator.domain.port;

import com.payments.orchestrator.domain.model.aggregate.Transaction;

import reactor.core.publisher.Mono;


public interface TransferRepositoryPort {

    Mono<Transaction> savePending(Transaction request);

    //Optional<Transaction> findById(UUID id);
}