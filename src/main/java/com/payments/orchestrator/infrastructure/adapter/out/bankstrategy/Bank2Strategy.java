package com.payments.orchestrator.infrastructure.adapter.out.bankstrategy;

import com.payments.orchestrator.domain.model.aggregate.Transaction;
import com.payments.orchestrator.domain.port.BankStrategy;
import com.payments.orchestrator.infrastructure.adapter.out.bankstrategy.dto.BankTransferRequest;
import com.payments.orchestrator.infrastructure.adapter.out.bankstrategy.mapper.BankRequestMapper;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component("BANCO_2")
public class Bank2Strategy implements BankStrategy {
   

    @Override
    public String getBankCode() {
        return "BANCO_2";
    }

    private final BankRequestMapper mapper = new BankRequestMapper();
    @Override
    public Mono<Boolean> sendTransfer(Transaction request) {
    BankTransferRequest bankRequest = mapper.toBankRequest(request);
        return Mono.fromCallable(() -> {
            // Simulación llamada bloqueante
            Thread.sleep(200);
            return true;
        }).subscribeOn(Schedulers.boundedElastic());
    }
}