package com.payments.orchestrator.infrastructure.config.constants;

public class HttpHeaderConstants {

    private HttpHeaderConstants() {
    }

    // Custom Headers
    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    public static final String REQUEST_ID_HEADER = "X-Request-Id";
    
    // Content Type Headers (for reference)
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_XML = "application/xml";
    
    // Authorization Headers (for future use)
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
}
