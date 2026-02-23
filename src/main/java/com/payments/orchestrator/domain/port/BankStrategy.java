package com.payments.orchestrator.domain.port;

import com.payments.orchestrator.domain.model.aggregate.Transaction;

import reactor.core.publisher.Mono;

public interface BankStrategy {

    String getBankCode();

    Mono<Boolean> sendTransfer(Transaction transaction);
}