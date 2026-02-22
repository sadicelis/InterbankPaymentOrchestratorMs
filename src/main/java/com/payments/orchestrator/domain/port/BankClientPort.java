
package com.payments.orchestrator.domain.port;

import reactor.core.publisher.Mono;

public interface BankClientPort {
    Mono<Boolean> sendTransfer(String bankCode, String reference, String source, String destination, String amount);
}
