
package com.payments.orchestrator.domain.model.enums;

import lombok.Getter;

@Getter
public enum TransactionStatus {
    PENDING("PENDIENTE"),
    SUCCESSFUL("EXITOSA"),
    FAILED("FALLIDA");

    private final String statusMessage;

    TransactionStatus(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public static TransactionStatus fromMessage(String message) {
        return java.util.Arrays.stream(TransactionStatus.values())
                .filter(s -> s.statusMessage.equalsIgnoreCase(message))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Estado no válido: " + message));
    }
}
