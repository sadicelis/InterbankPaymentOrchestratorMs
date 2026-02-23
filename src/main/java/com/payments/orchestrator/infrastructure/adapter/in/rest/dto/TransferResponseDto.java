package com.payments.orchestrator.infrastructure.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransferResponseDto {

    private String transactionId;

    private String externalReference;

    private String bankCode;

    private String sourceAccount;

    private String destinationAccount;

    private BigDecimal amount;

    private String status;

    private String statusMessage;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
