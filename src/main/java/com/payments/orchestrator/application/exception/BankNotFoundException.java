package com.payments.orchestrator.application.exception;

public class BankNotFoundException extends ApplicationException {

    public BankNotFoundException(String message) {
        super(message, "ERR_BANK_NOT_FOUND");
    }

    public BankNotFoundException(String message, Throwable cause) {
        super(message, "ERR_BANK_NOT_FOUND", cause);
    }
}
