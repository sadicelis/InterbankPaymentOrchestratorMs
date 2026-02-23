package com.payments.orchestrator.infrastructure.config.constants;

public class ErrorCodesConstants {

    private ErrorCodesConstants() {
    }

    // Domain errors (validación de negocio)
    public static final String ERR_INVALID_TRANSACTION = "ERR_INVALID_TRANSACTION";
    public static final String ERR_INSUFFICIENT_FUNDS = "ERR_INSUFFICIENT_FUNDS";

    // Application errors (orquestación)
    public static final String ERR_BANK_NOT_FOUND = "ERR_BANK_NOT_FOUND";
    public static final String ERR_STRATEGY_RESOLUTION = "ERR_STRATEGY_RESOLUTION";
    public static final String ERR_TRANSFER_PROCESSING = "ERR_TRANSFER_PROCESSING";

    // Infrastructure errors (técnicos)
    public static final String ERR_DB_ERROR = "ERR_DB_ERROR";
    public static final String ERR_BANK_UNAVAILABLE = "ERR_BANK_UNAVAILABLE";
    public static final String ERR_EXTERNAL_SERVICE = "ERR_EXTERNAL_SERVICE";
    public static final String ERR_TIMEOUT = "ERR_TIMEOUT";

    // Validation errors
    public static final String ERR_VALIDATION_FAILED = "ERR_VALIDATION_FAILED";

    // Generic errors
    public static final String ERR_INTERNAL = "ERR_INTERNAL";
    public static final String SUCCESS = "SUCCESS";
}
