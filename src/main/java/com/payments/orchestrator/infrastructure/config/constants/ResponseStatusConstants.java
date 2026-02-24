package com.payments.orchestrator.infrastructure.config.constants;

public class ResponseStatusConstants {

    private ResponseStatusConstants() {
    }

    // API Response Status
    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_ERROR = "ERROR";

    // Transaction Status
    public static final String TXN_STATUS_PENDING = "PENDING";
    public static final String TXN_STATUS_PROCESSING = "PROCESSING";
    public static final String TXN_STATUS_COMPLETED = "COMPLETED";
    public static final String TXN_STATUS_FAILED = "FAILED";
    public static final String TXN_STATUS_ERROR = "ERROR";

    // Bank Response Status
    public static final String BANK_STATUS_SUCCESS = "SUCCESS";
    public static final String BANK_STATUS_FAILURE = "FAILURE";
}
