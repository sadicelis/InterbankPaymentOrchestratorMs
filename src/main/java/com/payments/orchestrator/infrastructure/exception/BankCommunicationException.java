package com.payments.orchestrator.infrastructure.exception;

public class BankCommunicationException extends InfrastructureException {

    public BankCommunicationException(String message) {
        super(message, "ERR_BANK_COMMUNICATION");
    }

    public BankCommunicationException(String message, Throwable cause) {
        super(message, "ERR_BANK_COMMUNICATION", cause);
    }
}
