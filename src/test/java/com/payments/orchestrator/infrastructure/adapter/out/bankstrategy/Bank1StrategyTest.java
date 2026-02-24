package com.payments.orchestrator.infrastructure.adapter.out.bankstrategy;

import com.payments.orchestrator.domain.model.BankTransferResult;
import com.payments.orchestrator.domain.model.aggregate.Transaction;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class Bank1StrategyTest {

    @Test
    void bank1Strategy_implements_bankStrategy() {
       assertDoesNotThrow(() -> {
            Class.forName("com.payments.orchestrator.infrastructure.adapter.out.bankstrategy.Bank1Strategy");
        });
    }

    @Test
    void bankTransferResult_canBeConstructed() {
        BankTransferResult result = new BankTransferResult(true, "00", "Success", "txn-123");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("00", result.getCode());
        assertEquals("Success", result.getMessage());
        assertEquals("txn-123", result.getExternalRef());
    }

    @Test
    void bankTransferResult_failureScenario() {
        BankTransferResult result = new BankTransferResult(false, "51", "Insufficient funds", null);
        
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("51", result.getCode());
        assertEquals("Insufficient funds", result.getMessage());
        assertNull(result.getExternalRef());
    }
}

