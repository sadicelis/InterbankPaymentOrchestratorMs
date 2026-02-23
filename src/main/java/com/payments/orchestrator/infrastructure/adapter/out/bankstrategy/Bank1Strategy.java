package com.payments.orchestrator.infrastructure.adapter.out.bankstrategy;

import com.payments.orchestrator.domain.model.aggregate.Transaction;
import com.payments.orchestrator.domain.port.BankStrategy;
import com.payments.orchestrator.infrastructure.adapter.out.bankstrategy.dto.Bank1ResponseDto;
import com.payments.orchestrator.infrastructure.adapter.out.bankstrategy.dto.BankTransferRequest;
import com.payments.orchestrator.infrastructure.adapter.out.bankstrategy.mapper.BankRequestMapper;
import com.payments.orchestrator.infrastructure.config.BankClientProperties;
import com.payments.orchestrator.infrastructure.config.constants.BankCodesConstants;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component(BankCodesConstants.BANK_1)
@RequiredArgsConstructor
public class Bank1Strategy implements BankStrategy {
    @Override
    public String getBankCode() {
        return BankCodesConstants.BANK_1;
    }

    private final WebClient webClient;
    private final BankRequestMapper mapper;

@Autowired
    public Bank1Strategy(WebClient.Builder webClientBuilder,
            BankClientProperties properties,
            BankRequestMapper mapper) {

        // Obtenemos la configuración desde el Map que definiste
        var clientConfig = properties.getClients().get(BankCodesConstants.BANK_1);

        if (clientConfig == null) {
            throw new RuntimeException("Configuration for BANK_1 not found in properties");
        }

        this.webClient = webClientBuilder
                .baseUrl(clientConfig.getBaseUrl())
                .build();
        this.mapper = mapper;
    }

    @Override
    public Mono<Boolean> sendTransfer(Transaction request) {
        return webClient.post()
                .uri("/transfer")
                .bodyValue(mapper.toBankRequest(request))
                .retrieve()
                .bodyToMono(Bank1ResponseDto.class)
                .map(res -> "SUCCESS".equals(res.getStatus()))
                .onErrorResume(e -> {
                    return Mono.just(false);
                });
    }
}