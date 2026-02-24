package com.payments.orchestrator.infrastructure.config.constants;

public class BankResponseConstants {

    private BankResponseConstants() {
    }

    // Bank Response Status Strings
    public static final String BANK_STATUS_SUCCESS = "SUCCESS";
    public static final String BANK_STATUS_FAILURE = "FAILURE";
    
    // Bank Response Codes
    public static final String BANK_RESPONSE_SUCCESS_CODE = "00";
    public static final String BANK_RESPONSE_FAILURE_CODE = "01";
    
    // Bank Response Messages
    public static final String BANK_MSG_SUCCESS = "Transfer successful";
    public static final String BANK_MSG_FAILURE = "Transfer failed";
    public static final String BANK_MSG_TIMEOUT = "Request timeout";
    public static final String BANK_MSG_UNAVAILABLE = "Bank service unavailable";
    
    // Bank Response Transaction IDs (for mock responses)
    public static final String BANK_MOCK_TXN_ID = "txn_123";
    public static final String BANK_MOCK_TXN_PREFIX = "txn_";
    
    // Bank Processing Delays (milliseconds) - for mock implementations
    public static final int BANK_MOCK_DELAY_MS = 200;
    
    // Resilience Configuration Values
    public static final int BANK_REQUEST_TIMEOUT_SECONDS = 10;
    public static final int BANK_RETRY_MAX_ATTEMPTS = 3;
    public static final int BANK_RETRY_WAIT_DURATION_MS = 500;
    public static final int CIRCUIT_BREAKER_FAILURE_RATE = 50;
    public static final int CIRCUIT_BREAKER_MIN_CALLS = 5;
    public static final int CIRCUIT_BREAKER_WINDOW_SIZE = 10;
    public static final int CIRCUIT_BREAKER_WAIT_DURATION_SECONDS = 10;
    
    // HTTP Status Code Thresholds
    public static final int HTTP_STATUS_SERVER_ERROR_THRESHOLD = 500;
}
