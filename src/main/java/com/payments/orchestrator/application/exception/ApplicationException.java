package com.payments.orchestrator.application.exception;

public abstract class ApplicationException extends RuntimeException {

    private final String code;

    public ApplicationException(String message, String code) {
        super(message);
        this.code = code;
    }

    public ApplicationException(String message, String code, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
