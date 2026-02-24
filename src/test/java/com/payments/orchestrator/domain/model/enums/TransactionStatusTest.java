package com.payments.orchestrator.domain.model.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransactionStatusTest {

    @Test
    void transactionStatus_shouldHaveAllEnumValues() {
        TransactionStatus[] statuses = TransactionStatus.values();
        
        assertTrue(statuses.length > 0);
        assertTrue(statuses.length >= 3);
    }

    @Test
    void transactionStatus_PENDING_shouldExist() {
        TransactionStatus status = TransactionStatus.PENDING;
        assertNotNull(status);
        assertEquals("PENDING", status.name());
        assertEquals("PENDIENTE", status.getStatusMessage());
    }

    @Test
    void transactionStatus_SUCCESSFUL_shouldExist() {
        TransactionStatus status = TransactionStatus.SUCCESSFUL;
        assertNotNull(status);
        assertEquals("SUCCESSFUL", status.name());
        assertEquals("EXITOSA", status.getStatusMessage());
    }

    @Test
    void transactionStatus_FAILED_shouldExist() {
        TransactionStatus status = TransactionStatus.FAILED;
        assertNotNull(status);
        assertEquals("FAILED", status.name());
        assertEquals("FALLIDA", status.getStatusMessage());
    }

    @Test
    void transactionStatus_valueOf_shouldReturnCorrectValue() {
        TransactionStatus status = TransactionStatus.valueOf("PENDING");
        assertEquals(TransactionStatus.PENDING, status);
    }

    @Test
    void transactionStatus_canBeCompared() {
        TransactionStatus status1 = TransactionStatus.PENDING;
        TransactionStatus status2 = TransactionStatus.PENDING;
        TransactionStatus status3 = TransactionStatus.SUCCESSFUL;

        assertEquals(status1, status2);
        assertNotEquals(status1, status3);
    }

    @Test
    void transactionStatus_getStatusMessage() {
        assertEquals("PENDIENTE", TransactionStatus.PENDING.getStatusMessage());
        assertEquals("EXITOSA", TransactionStatus.SUCCESSFUL.getStatusMessage());
        assertEquals("FALLIDA", TransactionStatus.FAILED.getStatusMessage());
    }
}
