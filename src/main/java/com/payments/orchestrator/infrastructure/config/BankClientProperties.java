package com.payments.orchestrator.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "bank")
@Getter
@Setter
public class BankClientProperties {

    private Map<String, Client> clients;

    @Getter
    @Setter
    public static class Client {
        private String baseUrl;
    }
}