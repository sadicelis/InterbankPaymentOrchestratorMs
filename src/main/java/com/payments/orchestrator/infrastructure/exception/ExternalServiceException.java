package com.payments.orchestrator.infrastructure.exception;

public class ExternalServiceException extends InfrastructureException {

    private final Integer httpStatus;

    public ExternalServiceException(String message) {
        super(message, "ERR_EXTERNAL_SERVICE");
        this.httpStatus = null;
    }

    public ExternalServiceException(String message, Integer httpStatus) {
        super(message, "ERR_EXTERNAL_SERVICE");
        this.httpStatus = httpStatus;
    }

    public ExternalServiceException(String message, Throwable cause) {
        super(message, "ERR_EXTERNAL_SERVICE", cause);
        this.httpStatus = null;
    }

    public ExternalServiceException(String message, Integer httpStatus, Throwable cause) {
        super(message, "ERR_EXTERNAL_SERVICE", cause);
        this.httpStatus = httpStatus;
    }

    public Integer getHttpStatus() {
        return httpStatus;
    }
}
