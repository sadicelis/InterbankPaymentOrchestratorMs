package com.payments.orchestrator.infrastructure.adapter.in.rest;

import com.payments.orchestrator.application.service.TransferService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transfers")
@RequiredArgsConstructor
@Validated
public class TransferController {

    private final TransferService service;

    @PostMapping
    public ResponseEntity<UUID> transfer(@RequestBody @Validated TransferRequest request) {

        UUID id = service.process(request);

        return ResponseEntity.ok(id);
    }

    @Data
    public static class TransferRequest {

        @NotBlank
        private String reference;

        @NotBlank
        private String sourceAccount;

        @NotBlank
        private String destinationAccount;

        @NotNull
        private BigDecimal amount;

        @NotBlank
        private String bankCode;
    }
}