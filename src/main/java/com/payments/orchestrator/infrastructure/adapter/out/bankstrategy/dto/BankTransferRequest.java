package com.payments.orchestrator.infrastructure.adapter.out.bankstrategy.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class BankTransferRequest {
    private UUID transactionId;
    private String reference;
    private String sourceAccount;
    private String destinationAccount;
    private String amount;
}