package com.payments.orchestrator.infrastructure.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PersistenceExceptionTest {

    @Test
    void testPersistenceExceptionWithMessage() {
        String message = "Database connection failed";
        PersistenceException exception = new PersistenceException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testPersistenceExceptionCanBeThrown() {
        assertThrows(PersistenceException.class, () -> {
            throw new PersistenceException("Cannot save transaction");
        });
    }

    @Test
    void testPersistenceExceptionWithCause() {
        String message = "Persistence operation failed";
        Throwable cause = new org.hibernate.HibernateException("Session factory error");
        PersistenceException exception = new PersistenceException(message, cause);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testPersistenceExceptionInheritance() {
        PersistenceException exception = new PersistenceException("Test");
        assertTrue(exception instanceof Exception);
    }
}
