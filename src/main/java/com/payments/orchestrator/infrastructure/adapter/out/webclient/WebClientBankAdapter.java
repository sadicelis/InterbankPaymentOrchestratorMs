
package com.payments.orchestrator.infrastructure.adapter.out.webclient;

import com.payments.orchestrator.domain.port.BankClientPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class WebClientBankAdapter implements BankClientPort {

    private final WebClient.Builder builder;

    @Override
    public Mono<Boolean> sendTransfer(String bankCode, String reference, String source, String destination, String amount) {
        return builder.build()
                .post()
                .uri("http://bank-mock:8081/api/transfer")
                .bodyValue(new TransferPayload(reference, source, destination, amount))
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    record TransferPayload(String reference, String sourceAccount, String destinationAccount, String amount) {}
}
