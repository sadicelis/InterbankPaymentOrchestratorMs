
package com.payments.orchestrator.application.service;

import com.payments.orchestrator.domain.port.BankClientPort;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferApplicationService {

    private final BankClientPort bankClientPort;

    @CircuitBreaker(name = "bankService")
    @Retry(name = "bankService")
    public UUID process(String bankCode) {
        bankClientPort.sendTransfer(bankCode,"ref","src","dest","100").block();
        return UUID.randomUUID();
    }
}
