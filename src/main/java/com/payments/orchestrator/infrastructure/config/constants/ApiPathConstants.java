package com.payments.orchestrator.infrastructure.config.constants;

public class ApiPathConstants {

    private ApiPathConstants() {
    }

    // API Version and Base Path
    public static final String API_VERSION = "/api/v1";
    
    // Transfer Endpoints
    public static final String TRANSFERS_BASE = API_VERSION + "/transfers";
    public static final String TRANSFERS_PATH = "/transfers";
    
    // Bank Mock Endpoints
    public static final String MOCK_BANK_BASE = "/mock/bank";
    public static final String BANK_TRANSFER_PATH = "/transfer";
    
    // Health Check
    public static final String HEALTH_CHECK_PATH = "/health";
}
