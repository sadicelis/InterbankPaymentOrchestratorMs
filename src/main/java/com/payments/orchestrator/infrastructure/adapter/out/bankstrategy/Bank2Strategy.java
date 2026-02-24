package com.payments.orchestrator.infrastructure.adapter.out.bankstrategy;

import com.payments.orchestrator.domain.model.BankTransferResult;
import com.payments.orchestrator.domain.model.aggregate.Transaction;
import com.payments.orchestrator.domain.port.BankStrategy;
import com.payments.orchestrator.infrastructure.adapter.out.bankstrategy.mapper.BankRequestMapper;
import com.payments.orchestrator.infrastructure.config.BankClientProperties;
import com.payments.orchestrator.infrastructure.config.constants.BankCodesConstants;
import com.payments.orchestrator.infrastructure.config.constants.BankResponseConstants;
import com.payments.orchestrator.infrastructure.exception.ExternalServiceException;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component(BankCodesConstants.BANK_2)
public class Bank2Strategy implements BankStrategy {
    private final BankRequestMapper mapper;

    @Override
    public String getBankCode() {
        return BankCodesConstants.BANK_2;
    }

    public Bank2Strategy(WebClient.Builder webClientBuilder,
            BankClientProperties properties,
            BankRequestMapper mapper) {

        var clientConfig = properties.getClients().get(BankCodesConstants.BANK_2);

        if (clientConfig == null) {
            throw new ExternalServiceException(
                    "Configuration for BANK_2 not found in application properties"
            );
        }

        this.mapper = mapper;
    }

    @Override
    public Mono<BankTransferResult> sendTransfer(Transaction request) {
        return Mono.fromCallable(() -> {
            Thread.sleep(BankResponseConstants.BANK_MOCK_DELAY_MS);
            return new BankTransferResult(true, BankResponseConstants.BANK_RESPONSE_SUCCESS_CODE, BankResponseConstants.BANK_MSG_SUCCESS, BankResponseConstants.BANK_MOCK_TXN_ID);
        }).subscribeOn(Schedulers.boundedElastic());
    }
}