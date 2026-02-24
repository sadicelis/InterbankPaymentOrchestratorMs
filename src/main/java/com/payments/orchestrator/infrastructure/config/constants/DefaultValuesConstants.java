package com.payments.orchestrator.infrastructure.config.constants;

public class DefaultValuesConstants {

    private DefaultValuesConstants() {
    }

    // Default/Fallback Error Codes
    public static final String DEFAULT_ERROR_CODE = "ERR_UNKNOWN";
    
    // Default/Fallback Error Messages
    public static final String DEFAULT_ERROR_MESSAGE = "An unexpected error occurred";
    public static final String DEFAULT_BANK_ERROR_MESSAGE = "Bank returned failure";
    public static final String DEFAULT_VALIDATION_ERROR_MESSAGE = "Campo(s) inválido(s) en la solicitud";
    
    // Default/Fallback String Values
    public static final String DEFAULT_UNKNOWN = "UNKNOWN";
    public static final String DEFAULT_NOT_AVAILABLE = "N/A";
    public static final String DEFAULT_EMPTY_STRING = "";
    
    // Default/Fallback Numeric Values
    public static final int DEFAULT_ZERO = 0;
    public static final long DEFAULT_TIMESTAMP_ZERO = 0L;
    
    // Operation Messages
    public static final String MSG_TRANSFER_REGISTERED = "Transfer registered and pending processing";
    public static final String MSG_TRANSFER_PROCESSED = "Transfer request processed successfully";
    
    // Web Request Path Extraction
    public static final String REQUEST_PATH_PREFIX = "uri=";
    public static final int REQUEST_PATH_PREFIX_LENGTH = 4; // length of "uri="
}
