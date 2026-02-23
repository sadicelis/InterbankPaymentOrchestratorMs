package com.payments.orchestrator.domain.exception;

public class InsufficientFundsException extends DomainException {

    public InsufficientFundsException(String message) {
        super(message, "ERR_INSUFFICIENT_FUNDS");
    }

    public InsufficientFundsException(String message, Throwable cause) {
        super(message, "ERR_INSUFFICIENT_FUNDS", cause);
    }
}
