package com.payments.orchestrator.infrastructure.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BankCommunicationExceptionTest {

    @Test
    void testBankCommunicationExceptionWithMessage() {
        String message = "Failed to connect to bank API";
        BankCommunicationException exception = new BankCommunicationException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testBankCommunicationExceptionCanBeThrown() {
        assertThrows(BankCommunicationException.class, () -> {
            throw new BankCommunicationException("Connection timeout");
        });
    }

    @Test
    void testBankCommunicationExceptionWithCause() {
        String message = "Bank communication error";
        Throwable cause = new java.net.ConnectException("Network unreachable");
        BankCommunicationException exception = new BankCommunicationException(message, cause);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testBankCommunicationExceptionInheritance() {
        BankCommunicationException exception = new BankCommunicationException("Test");
        assertTrue(exception instanceof Exception);
    }
}
