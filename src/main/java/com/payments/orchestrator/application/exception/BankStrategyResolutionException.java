package com.payments.orchestrator.application.exception;

public class BankStrategyResolutionException extends ApplicationException {

    public BankStrategyResolutionException(String message) {
        super(message, "ERR_STRATEGY_RESOLUTION");
    }

    public BankStrategyResolutionException(String message, Throwable cause) {
        super(message, "ERR_STRATEGY_RESOLUTION", cause);
    }
}
