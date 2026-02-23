package com.payments.orchestrator.infrastructure.adapter.out.bankstrategy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Bank1ResponseDto {
    private String status;
    private String transactionId;
    private String message;
}