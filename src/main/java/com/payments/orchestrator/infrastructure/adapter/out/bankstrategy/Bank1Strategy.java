package com.payments.orchestrator.infrastructure.adapter.out.bankstrategy;

import com.payments.orchestrator.domain.model.BankTransferResult;
import com.payments.orchestrator.domain.model.aggregate.Transaction;
import com.payments.orchestrator.domain.port.BankStrategy;
import com.payments.orchestrator.infrastructure.adapter.out.bankstrategy.dto.Bank1ResponseDto;
import com.payments.orchestrator.infrastructure.adapter.out.bankstrategy.mapper.BankRequestMapper;
import com.payments.orchestrator.infrastructure.config.BankClientProperties;
import com.payments.orchestrator.infrastructure.config.constants.BankCodesConstants;
import com.payments.orchestrator.infrastructure.config.constants.ApiPathConstants;
import com.payments.orchestrator.infrastructure.config.constants.BankResponseConstants;
import com.payments.orchestrator.infrastructure.exception.ExternalServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class Bank1Strategy implements BankStrategy {

    private final WebClient webClient;
    private final BankRequestMapper mapper;

    public Bank1Strategy(WebClient.Builder webClientBuilder,
                         BankClientProperties properties,
                         BankRequestMapper mapper) {

        var clientConfig = properties.getClients().get(BankCodesConstants.BANK_1);
        if (clientConfig == null) {
            throw new ExternalServiceException(
                    "Configuration for BANK_1 not found in application properties"
            );
        }

        this.webClient = webClientBuilder
                .baseUrl(clientConfig.getBaseUrl())
                .build();
        this.mapper = mapper;
    }

    @Override
    public String getBankCode() {
        return BankCodesConstants.BANK_1;
    }

    @Override
    public Mono<BankTransferResult> sendTransfer(Transaction request) {
        return webClient.post()
                .uri(ApiPathConstants.BANK_TRANSFER_PATH)
                .bodyValue(mapper.toBankRequest(request))
                .retrieve()
                .bodyToMono(Bank1ResponseDto.class)
                .map(res -> new BankTransferResult(
                        BankResponseConstants.BANK_STATUS_SUCCESS.equalsIgnoreCase(res.getStatus()),
                        res.getCode(),
                        res.getMessage(),
                        res.getTransactionId()
                ));
    }
}