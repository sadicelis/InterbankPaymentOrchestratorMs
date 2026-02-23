package com.payments.orchestrator.infrastructure.adapter.out.bankstrategy;

import com.payments.orchestrator.domain.model.aggregate.Transaction;
import com.payments.orchestrator.domain.port.BankStrategy;
import com.payments.orchestrator.infrastructure.adapter.out.bankstrategy.dto.BankTransferRequest;
import com.payments.orchestrator.infrastructure.adapter.out.bankstrategy.mapper.BankRequestMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component("BANCO_1")
@RequiredArgsConstructor
public class Bank1Strategy implements BankStrategy {
    @Override
    public String getBankCode() {
        return "BANCO_1"; 
    }

    private final WebClient webClient;
    private final BankRequestMapper mapper;

    @Override
    public Mono<Boolean> sendTransfer(Transaction request) {

        BankTransferRequest bankRequest = mapper.toBankRequest(request);

        return webClient.post()
                .uri("/transfer")
                .bodyValue(bankRequest)
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> true);
    }
}