package com.payments.orchestrator.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BankTransferResultTest {

    @Test
    void constructor_withAllParameters_shouldInitializeCorrectly() {
        BankTransferResult result = new BankTransferResult(true, "00", "Success", "txn-123");

        assertTrue(result.isSuccess());
        assertEquals("00", result.getCode());
        assertEquals("Success", result.getMessage());
        assertEquals("txn-123", result.getExternalRef());
    }

    @Test
    void constructor_withFailureFlag_shouldInitializeCorrectly() {
        BankTransferResult result = new BankTransferResult(false, "01", "Failure", null);

        assertFalse(result.isSuccess());
        assertEquals("01", result.getCode());
        assertEquals("Failure", result.getMessage());
        assertNull(result.getExternalRef());
    }

    @Test
    void isSuccess_returnsCorrectValue() {
        BankTransferResult successResult = new BankTransferResult(true, "00", "OK", "txn-1");
        BankTransferResult failureResult = new BankTransferResult(false, "99", "ERROR", null);

        assertTrue(successResult.isSuccess());
        assertFalse(failureResult.isSuccess());
    }

    @Test
    void getCode_returnsCorrectCode() {
        String code = "ERR_123";
        BankTransferResult result = new BankTransferResult(false, code, "Error message", null);

        assertEquals(code, result.getCode());
    }

    @Test
    void getMessage_returnsCorrectMessage() {
        String message = "Transaction processed successfully";
        BankTransferResult result = new BankTransferResult(true, "00", message, "txn-1");

        assertEquals(message, result.getMessage());
    }

    @Test
    void getExternalRef_returnsCorrectId() {
        String externalRef = "EXT-TXN-789";
        BankTransferResult result = new BankTransferResult(true, "00", "OK", externalRef);

        assertEquals(externalRef, result.getExternalRef());
    }

    @Test
    void externalRef_canBeNull() {
        BankTransferResult result = new BankTransferResult(false, "01", "Failure", null);

        assertNull(result.getExternalRef());
    }

    @Test
    void constructor_withEmptyStrings() {
        BankTransferResult result = new BankTransferResult(true, "", "", "");

        assertTrue(result.isSuccess());
        assertEquals("", result.getCode());
        assertEquals("", result.getMessage());
        assertEquals("", result.getExternalRef());
    }

    @Test
    void constructor_withMultipleCallsIndependent() {
        BankTransferResult result1 = new BankTransferResult(true, "00", "OK", "txn-1");
        BankTransferResult result2 = new BankTransferResult(false, "01", "FAIL", "txn-2");

        assertNotEquals(result1.isSuccess(), result2.isSuccess());
        assertNotEquals(result1.getCode(), result2.getCode());
        assertNotEquals(result1.getExternalRef(), result2.getExternalRef());
    }
}

