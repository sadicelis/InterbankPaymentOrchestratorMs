package com.payments.orchestrator.infrastructure.exception;

public abstract class InfrastructureException extends RuntimeException {

    private final String code;

    public InfrastructureException(String message, String code) {
        super(message);
        this.code = code;
    }

    public InfrastructureException(String message, String code, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
