package com.payments.orchestrator.application.service;

import com.payments.orchestrator.domain.port.BankStrategy;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class BankStrategyResolver {

    private final Map<String, BankStrategy> strategies;
    
    @Autowired
    public BankStrategyResolver(List<BankStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(
                    BankStrategy::getBankCode, 
                    strategy -> strategy
                ));
    }

    public BankStrategy resolve(String bankCode) {
        return Optional.ofNullable(strategies.get(bankCode))
                .orElseThrow(() -> new RuntimeException("Bank strategy not found for: " + bankCode));
    }

    @PostConstruct
    public void init() {
        log.info("Estrategias cargadas: {}",
                strategies.values().stream().map(BankStrategy::getBankCode).toList());
    }
}