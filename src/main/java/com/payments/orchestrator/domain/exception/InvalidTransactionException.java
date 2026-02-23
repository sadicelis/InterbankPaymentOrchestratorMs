package com.payments.orchestrator.domain.exception;

public class InvalidTransactionException extends DomainException {

    public InvalidTransactionException(String message) {
        super(message, "ERR_INVALID_TRANSACTION");
    }

    public InvalidTransactionException(String message, Throwable cause) {
        super(message, "ERR_INVALID_TRANSACTION", cause);
    }
}
