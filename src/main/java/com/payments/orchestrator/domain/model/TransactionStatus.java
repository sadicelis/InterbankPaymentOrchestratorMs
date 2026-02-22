
package com.payments.orchestrator.domain.model;

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

}
