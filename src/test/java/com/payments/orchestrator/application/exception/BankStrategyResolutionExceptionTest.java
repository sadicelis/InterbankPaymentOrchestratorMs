package com.payments.orchestrator.application.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BankStrategyResolutionExceptionTest {

    @Test
    void testBankStrategyResolutionExceptionWithMessage() {
        String message = "Duplicate strategy for BANK_1";
        BankStrategyResolutionException exception = new BankStrategyResolutionException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testBankStrategyResolutionExceptionCanBeThrown() {
        assertThrows(BankStrategyResolutionException.class, () -> {
            throw new BankStrategyResolutionException("Strategy resolution failed");
        });
    }

    @Test
    void testBankStrategyResolutionExceptionWithCause() {
        String message = "Cannot resolve strategy";
        Throwable cause = new IllegalArgumentException("Invalid bank code");
        BankStrategyResolutionException exception = new BankStrategyResolutionException(message, cause);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testBankStrategyResolutionExceptionInheritance() {
        BankStrategyResolutionException exception = new BankStrategyResolutionException("Test");
        assertTrue(exception instanceof Exception);
    }
}
