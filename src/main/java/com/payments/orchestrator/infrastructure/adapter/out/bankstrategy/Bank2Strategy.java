package com.payments.orchestrator.infrastructure.adapter.out.bankstrategy;

import com.payments.orchestrator.domain.model.BankTransferResult;
import com.payments.orchestrator.domain.model.aggregate.Transaction;
import com.payments.orchestrator.domain.port.BankStrategy;
import com.payments.orchestrator.infrastructure.adapter.out.bankstrategy.dto.BankTransferRequest;
import com.payments.orchestrator.infrastructure.adapter.out.bankstrategy.mapper.BankRequestMapper;
import com.payments.orchestrator.infrastructure.config.BankClientProperties;
import com.payments.orchestrator.infrastructure.config.constants.BankCodesConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component(BankCodesConstants.BANK_2)
public class Bank2Strategy implements BankStrategy {
    private final WebClient webClient;
    private final BankRequestMapper mapper;

    @Override
    public String getBankCode() {
        return BankCodesConstants.BANK_2;
    }

    @Autowired
    public Bank2Strategy(WebClient.Builder webClientBuilder,
            BankClientProperties properties,
            BankRequestMapper mapper) {

        var clientConfig = properties.getClients().get(BankCodesConstants.BANK_2);

        if (clientConfig == null) {
            throw new RuntimeException("Configuration for BANK_2 not found in properties");
        }

        this.webClient = webClientBuilder
                .baseUrl(clientConfig.getBaseUrl())
                .build();
        this.mapper = mapper;
    }

    @Override
    public Mono<BankTransferResult> sendTransfer(Transaction request) {
        BankTransferRequest bankRequest = mapper.toBankRequest(request);
        return Mono.fromCallable(() -> {
            // Simulación llamada bloqueante
            Thread.sleep(200);
            return new BankTransferResult(true, "00", "Transfer successful", "txn_123");
        }).subscribeOn(Schedulers.boundedElastic());
    }
}