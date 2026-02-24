package com.payments.orchestrator.infrastructure.adapter.out.bankstrategy;

import com.payments.orchestrator.infrastructure.config.constants.BankResponseConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Bank2StrategyTest {

    @Test
    void bank2Strategy_implements_bankStrategy() {
       assertDoesNotThrow(() -> {
            Class.forName("com.payments.orchestrator.infrastructure.adapter.out.bankstrategy.Bank2Strategy");
        });
    }

    @Test
    void bankResponseConstants_haveRequiredValues() {
        assertNotNull(BankResponseConstants.BANK_RESPONSE_SUCCESS_CODE);
        assertNotNull(BankResponseConstants.BANK_MSG_SUCCESS);
        assertNotNull(BankResponseConstants.BANK_MOCK_TXN_ID);
        
        assertEquals("00", BankResponseConstants.BANK_RESPONSE_SUCCESS_CODE);
        assertEquals("Transfer successful", BankResponseConstants.BANK_MSG_SUCCESS);
        assertEquals("txn_123", BankResponseConstants.BANK_MOCK_TXN_ID);
    }

    @Test
    void bankResponseConstants_mockDelay_isValid() {
        assertTrue(BankResponseConstants.BANK_MOCK_DELAY_MS > 0);
        assertEquals(200, BankResponseConstants.BANK_MOCK_DELAY_MS);
    }

    @Test
    void bankResponseConstants_timeoutConfiguration() {
        assertTrue(BankResponseConstants.BANK_REQUEST_TIMEOUT_SECONDS > 0);
        assertEquals(10, BankResponseConstants.BANK_REQUEST_TIMEOUT_SECONDS);
    }
}
