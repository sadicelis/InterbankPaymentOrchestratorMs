package com.payments.orchestrator.infrastructure.exception;

public class PersistenceException extends InfrastructureException {

    public PersistenceException(String message) {
        super(message, "ERR_PERSISTENCE");
    }

    public PersistenceException(String message, Throwable cause) {
        super(message, "ERR_PERSISTENCE", cause);
    }
}
