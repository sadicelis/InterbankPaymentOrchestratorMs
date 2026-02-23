package com.payments.orchestrator.application.service;

import com.payments.orchestrator.application.exception.BankNotFoundException;
import com.payments.orchestrator.application.exception.BankStrategyResolutionException;
import com.payments.orchestrator.domain.port.BankStrategy;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class BankStrategyResolver {

    private final Map<String, BankStrategy> strategies;

    public BankStrategyResolver(List<BankStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toUnmodifiableMap(
                        BankStrategy::getBankCode,
                        Function.identity(),
                        (a, b) -> {
                            throw new BankStrategyResolutionException(
                                    "Duplicate BankStrategy found for bank code: " + a.getBankCode()
                            );
                        }
                ));
    }

    public BankStrategy resolve(String bankCode) {
        BankStrategy strategy = strategies.get(bankCode);
        if (strategy == null) {
            throw new BankNotFoundException(
                    "No strategy registered for bank code: " + bankCode
            );
        }
        return strategy;
    }

    @PostConstruct
    public void init() {
        log.info("Strategies loaded: {}", strategies.keySet());
    }
}