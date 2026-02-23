package com.payments.orchestrator.infrastructure.exception;

public class PersistenceException extends InfrastructureException {

    public PersistenceException(String message) {
        super(message, "ERR_DB_ERROR");
    }

    public PersistenceException(String message, Throwable cause) {
        super(message, "ERR_DB_ERROR", cause);
    }
}
