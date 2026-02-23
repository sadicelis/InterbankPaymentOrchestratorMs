package com.payments.orchestrator.domain.model.aggregate;

import com.payments.orchestrator.domain.model.enums.TransactionStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    void create_shouldInitializeAsPending_andSetTimestamps() {
        Transaction tx = Transaction.create(
                "ref-001",
                "1001",
                "2002",
                new BigDecimal("10.50"),
                "BANK_1"
        );

        assertNotNull(tx.getId());
        assertEquals("ref-001", tx.getReference());
        assertEquals("1001", tx.getSourceAccount());
        assertEquals("2002", tx.getDestinationAccount());
        assertEquals(new BigDecimal("10.50"), tx.getAmount());
        assertEquals("BANK_1", tx.getBankId());

        assertEquals(TransactionStatus.PENDING, tx.getStatus());
        assertNull(tx.getErrorMessage());

        assertNotNull(tx.getCreatedAt());
        assertNotNull(tx.getUpdatedAt());
    }

    @Test
    void restore_shouldRespectCreatedAtAndUpdatedAtAndStatus() {
        UUID id = UUID.randomUUID();
        LocalDateTime created = LocalDateTime.now().minusDays(1);
        LocalDateTime updated = LocalDateTime.now().minusHours(2);

        Transaction tx = Transaction.restore(
                id,
                "ref-restore",
                "A",
                "B",
                new BigDecimal("99.99"),
                "BANK_1",
                TransactionStatus.FAILED,
                "some-error",
                created,
                updated
        );

        assertEquals(id, tx.getId());
        assertEquals(TransactionStatus.FAILED, tx.getStatus());
        assertEquals("some-error", tx.getErrorMessage());
        assertEquals(created, tx.getCreatedAt(), "createdAt must be the restored value");
        assertEquals(updated, tx.getUpdatedAt(), "updatedAt must be the restored value");
    }

    @Test
    void markSuccessful_shouldSetStatusAndClearError_andUpdateTimestamp() {
        Transaction tx = Transaction.create("ref", "A", "B", new BigDecimal("1.00"), "BANK_1");
        LocalDateTime before = tx.getUpdatedAt();

        tx.markSuccessful();

        assertEquals(TransactionStatus.SUCCESSFUL, tx.getStatus());
        assertNull(tx.getErrorMessage());
        assertTrue(tx.getUpdatedAt().isAfter(before) || tx.getUpdatedAt().isEqual(before));
    }

    @Test
    void markFailed_shouldSetStatusAndError_andUpdateTimestamp() {
        Transaction tx = Transaction.create("ref", "A", "B", new BigDecimal("1.00"), "BANK_1");
        LocalDateTime before = tx.getUpdatedAt();

        tx.markFailed("bank down");

        assertEquals(TransactionStatus.FAILED, tx.getStatus());
        assertEquals("bank down", tx.getErrorMessage());
        assertTrue(tx.getUpdatedAt().isAfter(before) || tx.getUpdatedAt().isEqual(before));
    }
}