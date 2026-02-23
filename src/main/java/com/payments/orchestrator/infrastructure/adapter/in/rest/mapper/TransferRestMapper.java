package com.payments.orchestrator.infrastructure.adapter.in.rest.mapper;

import org.springframework.stereotype.Component;

import com.payments.orchestrator.domain.model.aggregate.Transaction;
import com.payments.orchestrator.infrastructure.adapter.in.rest.dto.TransferRequestDto;

@Component
public class TransferRestMapper {

    public static Transaction toDomain(TransferRequestDto dto) {
        return Transaction.create(
                dto.getReference(),
                dto.getSourceAccount(),
                dto.getDestinationAccount(),
                dto.getAmount(),
                dto.getBankCode()
        );
    }
}