package com.payments.orchestrator.application.exception;

public class TransferProcessingException extends ApplicationException {

    public TransferProcessingException(String message) {
        super(message, "ERR_TRANSFER_PROCESSING");
    }

    public TransferProcessingException(String message, Throwable cause) {
        super(message, "ERR_TRANSFER_PROCESSING", cause);
    }
}
