package com.payments.orchestrator.infrastructure.adapter.out.bankstrategy.mapper;

import org.springframework.stereotype.Component;

import com.payments.orchestrator.domain.model.aggregate.Transaction;
import com.payments.orchestrator.infrastructure.adapter.out.bankstrategy.dto.BankTransferRequest;

@Component
public class BankRequestMapper {

    public BankTransferRequest toBankRequest(Transaction transaction) {
        return BankTransferRequest.builder()
                .transactionId(transaction.getId())
                .reference(transaction.getReference())
                .sourceAccount(transaction.getSourceAccount())
                .destinationAccount(transaction.getDestinationAccount())
                .amount(transaction.getAmount().toString())
                .build();
    }
}