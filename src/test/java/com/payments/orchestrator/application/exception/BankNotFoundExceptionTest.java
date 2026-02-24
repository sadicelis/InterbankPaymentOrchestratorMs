package com.payments.orchestrator.application.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BankNotFoundExceptionTest {

    @Test
    void testBankNotFoundExceptionWithMessage() {
        String message = "Bank BANK_XYZ not found";
        BankNotFoundException exception = new BankNotFoundException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testBankNotFoundExceptionCanBeThrown() {
        String message = "UNKNOWN_BANK";
        
        assertThrows(BankNotFoundException.class, () -> {
            throw new BankNotFoundException(message);
        });
    }

    @Test
    void testBankNotFoundExceptionWithCause() {
        String message = "Bank not found";
        Throwable cause = new RuntimeException("Underlying cause");
        BankNotFoundException exception = new BankNotFoundException(message, cause);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}
