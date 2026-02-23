package com.payments.orchestrator.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class WebClientFactory {

    private final WebClient.Builder builder;
    private final BankClientProperties properties;

    public WebClient create(String bankCode) {

        String baseUrl = properties.getClients()
                .get(bankCode)
                .getBaseUrl();

        return builder
                .baseUrl(baseUrl)
                .build();
    }
}