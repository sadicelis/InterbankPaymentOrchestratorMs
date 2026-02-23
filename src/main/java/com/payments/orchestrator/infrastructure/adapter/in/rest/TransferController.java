package com.payments.orchestrator.infrastructure.adapter.in.rest;

import com.payments.orchestrator.application.service.TransferApplicationService;
import com.payments.orchestrator.domain.model.aggregate.Transaction;
import com.payments.orchestrator.infrastructure.adapter.in.rest.dto.TransferRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferApplicationService service;

    @PostMapping
    public Mono<ResponseEntity<UUID>> transfer(
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
                .map(ResponseEntity::ok);
    }
}