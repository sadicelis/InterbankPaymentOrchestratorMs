package com.payments.orchestrator.infrastructure.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExternalServiceExceptionTest {

    @Test
    void shouldThrowExceptionWithMessage() {
        String message = "External service unavailable";

        assertThatThrownBy(() -> {
            throw new ExternalServiceException(message);
        }).isInstanceOf(ExternalServiceException.class)
                .hasMessage(message);
    }

    @Test
    void shouldThrowExceptionWithMessageAndHttpStatus() {
        String message = "Service Unavailable";
        Integer httpStatus = 503;

        assertThatThrownBy(() -> {
            throw new ExternalServiceException(message, httpStatus);
        }).isInstanceOf(ExternalServiceException.class)
                .hasMessage(message);
    }

    @Test
    void shouldThrowExceptionWithCause() {
        String message = "Connection failed";
        Throwable cause = new RuntimeException("Network error");

        assertThatThrownBy(() -> {
            throw new ExternalServiceException(message, cause);
        }).isInstanceOf(ExternalServiceException.class)
                .hasMessage(message)
                .hasCause(cause);
    }

    @Test
    void shouldThrowExceptionWithMessageStatusAndCause() {
        String message = "Gateway timeout";
        Integer httpStatus = 504;
        Throwable cause = new RuntimeException("Request timeout");

        ExternalServiceException exception = new ExternalServiceException(message, httpStatus, cause);

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getHttpStatus()).isEqualTo(httpStatus);
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    void shouldGetHttpStatus() {
        ExternalServiceException exception = new ExternalServiceException("Error", 502);

        assertThat(exception.getHttpStatus()).isEqualTo(502);
    }

    @Test
    void shouldPreserveHttpStatusForRetry() {
        ExternalServiceException exception1 = new ExternalServiceException("Timeout", 504);
        ExternalServiceException exception2 = new ExternalServiceException("Bad Gateway", 502);

        assertThat(exception1.getHttpStatus()).isEqualTo(504);
        assertThat(exception2.getHttpStatus()).isEqualTo(502);
    }
}
