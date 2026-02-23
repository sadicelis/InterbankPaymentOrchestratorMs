package com.payments.orchestrator.infrastructure.adapter.in.rest;

import com.payments.orchestrator.application.service.TransferApplicationService;
import com.payments.orchestrator.domain.model.aggregate.Transaction;
import com.payments.orchestrator.infrastructure.adapter.in.rest.dto.TransferRequestDto;
import com.payments.orchestrator.infrastructure.adapter.in.rest.dto.TransferResponseDto;
import com.payments.orchestrator.infrastructure.adapter.in.rest.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferApplicationService service;

    @PostMapping
    public Mono<ResponseEntity<ApiResponse<TransferResponseDto>>> transfer(
            @Valid @RequestBody TransferRequestDto request
    ) {

        Transaction transaction = Transaction.create(
                request.getReference(),               
                request.getSourceAccount(),
                request.getDestinationAccount(),
                request.getAmount(),
                request.getBankCode()
        );

        return service.process(transaction)
                .map(transactionId -> {
                    TransferResponseDto response = TransferResponseDto.builder()
                            .transactionId(transactionId.toString())
                            .bankCode(request.getBankCode())
                            .sourceAccount(request.getSourceAccount())
                            .destinationAccount(request.getDestinationAccount())
                            .amount(request.getAmount())
                            .status("PENDING")
                            .statusMessage("Transfer registered and pending processing")
                            .build();

                    ApiResponse<TransferResponseDto> apiResponse = ApiResponse.success(
                            response,
                            "Transfer request processed successfully"
                    );
                    return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
                });
    }
}