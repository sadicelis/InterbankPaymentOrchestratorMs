package com.payments.orchestrator.infrastructure.adapter.out.bankstrategy.mapper;

import com.payments.orchestrator.domain.model.aggregate.Transaction;
import com.payments.orchestrator.infrastructure.adapter.out.bankstrategy.dto.BankTransferRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BankRequestMapperTest {

    private BankRequestMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new BankRequestMapper();
    }

    @Test
    void toBankRequest_shouldMapAllTransactionFields() {
       
        Transaction transaction = Transaction.create("ref-123", "ACC001", "ACC002", 
                new BigDecimal("1500.50"), "BANK_1");

        BankTransferRequest request = mapper.toBankRequest(transaction);

        assertNotNull(request);
        assertEquals(transaction.getSourceAccount(), request.getSourceAccount());
        assertEquals(transaction.getDestinationAccount(), request.getDestinationAccount());
        assertEquals(transaction.getAmount().toString(), request.getAmount());
        assertEquals(transaction.getReference(), request.getReference());
    }

    @Test
    void toBankRequest_withSmallAmount_shouldMapCorrectly() {

        Transaction transaction = Transaction.create("ref-small", "ACC001", "ACC002", 
                new BigDecimal("0.01"), "BANK_1");

        BankTransferRequest request = mapper.toBankRequest(transaction);

        assertNotNull(request);
        assertEquals("0.01", request.getAmount());
    }

    @Test
    void toBankRequest_withLargeAmount_shouldMapCorrectly() {

        Transaction transaction = Transaction.create("ref-large", "ACC001", "ACC002", 
                new BigDecimal("999999999.99"), "BANK_1");


        BankTransferRequest request = mapper.toBankRequest(transaction);

        assertNotNull(request);
        assertEquals("999999999.99", request.getAmount());
    }

    @Test
    void toBankRequest_withSpecialCharactersInReference_shouldPreserve() {

        Transaction transaction = Transaction.create("ref-2024-001_test", "ACC001", "ACC002", 
                new BigDecimal("1000.00"), "BANK_1");

        BankTransferRequest request = mapper.toBankRequest(transaction);

        assertNotNull(request);
        assertEquals("ref-2024-001_test", request.getReference());
    }

    @Test
    void toBankRequest_withDifferentBankCodes_shouldMapTransactionData() {

        String[] bankCodes = {"BANK_1", "BANK_2", "BANK_3"};
        
        for (String bankCode : bankCodes) {
            Transaction transaction = Transaction.create("ref-" + bankCode, "ACC001", "ACC002", 
                    new BigDecimal("500.00"), bankCode);

            BankTransferRequest request = mapper.toBankRequest(transaction);

            assertNotNull(request);
            assertEquals(transaction.getAmount().toString(), request.getAmount());
            assertEquals(transaction.getReference(), request.getReference());
        }
    }

    @Test
    void toBankRequest_accountsPreserved() {

        String sourceAccount = "1234567890";
        String destinationAccount = "0987654321";
        Transaction transaction = Transaction.create("ref-acc-test", sourceAccount, destinationAccount, 
                new BigDecimal("1000.00"), "BANK_1");

        BankTransferRequest request = mapper.toBankRequest(transaction);

        assertNotNull(request);
        assertEquals(sourceAccount, request.getSourceAccount());
        assertEquals(destinationAccount, request.getDestinationAccount());
    }

    @Test
    void toBankRequest_notNull_shouldNeverReturnNull() {

        Transaction transaction = Transaction.create("ref-null-test", "ACC001", "ACC002", 
                new BigDecimal("100.00"), "BANK_1");

        BankTransferRequest request = mapper.toBankRequest(transaction);

        assertNotNull(request);
        assertNotNull(request.getSourceAccount());
        assertNotNull(request.getDestinationAccount());
        assertNotNull(request.getAmount());
        assertNotNull(request.getReference());
    }
}