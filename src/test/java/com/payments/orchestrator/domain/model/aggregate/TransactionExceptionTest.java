package com.payments.orchestrator.domain.model.aggregate;

import com.payments.orchestrator.domain.exception.InvalidTransactionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Transaction Domain Validations Tests")
class TransactionExceptionTest {

    @Test
    @DisplayName("Debe lanzar InvalidTransactionException si reference es nulo")
    void testCreateWithNullReference() {
        assertThatThrownBy(() ->
                Transaction.create(
                        null,
                        "1234567890",
                        "0987654321",
                        BigDecimal.valueOf(100),
                        "BANK_1"
                )
        )
                .isInstanceOf(InvalidTransactionException.class)
                .hasMessageContaining("reference");
    }

    @Test
    @DisplayName("Debe lanzar InvalidTransactionException si sourceAccount es nulo")
    void testCreateWithNullSourceAccount() {
        assertThatThrownBy(() ->
                Transaction.create(
                        "REF123",
                        null,
                        "0987654321",
                        BigDecimal.valueOf(100),
                        "BANK_1"
                )
        )
                .isInstanceOf(InvalidTransactionException.class)
                .hasMessageContaining("Source account");
    }

    @Test
    @DisplayName("Debe lanzar InvalidTransactionException si destinationAccount es nulo")
    void testCreateWithNullDestinationAccount() {
        assertThatThrownBy(() ->
                Transaction.create(
                        "REF123",
                        "1234567890",
                        null,
                        BigDecimal.valueOf(100),
                        "BANK_1"
                )
        )
                .isInstanceOf(InvalidTransactionException.class)
                .hasMessageContaining("Destination account");
    }

    @Test
    @DisplayName("Debe lanzar InvalidTransactionException si sourceAccount equals destinationAccount")
    void testCreateWithSameSourceAndDestinationAccount() {
        assertThatThrownBy(() ->
                Transaction.create(
                        "REF123",
                        "1234567890",
                        "1234567890",
                        BigDecimal.valueOf(100),
                        "BANK_1"
                )
        )
                .isInstanceOf(InvalidTransactionException.class)
                .hasMessageContaining("cannot be the same");
    }

    @Test
    @DisplayName("Debe lanzar InvalidTransactionException si amount es nulo")
    void testCreateWithNullAmount() {
        assertThatThrownBy(() ->
                Transaction.create(
                        "REF123",
                        "1234567890",
                        "0987654321",
                        null,
                        "BANK_1"
                )
        )
                .isInstanceOf(InvalidTransactionException.class)
                .hasMessageContaining("Amount");
    }

    @Test
    @DisplayName("Debe lanzar InvalidTransactionException si amount es cero")
    void testCreateWithZeroAmount() {
        assertThatThrownBy(() ->
                Transaction.create(
                        "REF123",
                        "1234567890",
                        "0987654321",
                        BigDecimal.ZERO,
                        "BANK_1"
                )
        )
                .isInstanceOf(InvalidTransactionException.class)
                .hasMessageContaining("greater than zero");
    }

    @Test
    @DisplayName("Debe lanzar InvalidTransactionException si amount es negativo")
    void testCreateWithNegativeAmount() {
        assertThatThrownBy(() ->
                Transaction.create(
                        "REF123",
                        "1234567890",
                        "0987654321",
                        BigDecimal.valueOf(-100),
                        "BANK_1"
                )
        )
                .isInstanceOf(InvalidTransactionException.class)
                .hasMessageContaining("greater than zero");
    }

    @Test
    @DisplayName("Debe lanzar InvalidTransactionException si bankId es nulo")
    void testCreateWithNullBankId() {
        assertThatThrownBy(() ->
                Transaction.create(
                        "REF123",
                        "1234567890",
                        "0987654321",
                        BigDecimal.valueOf(100),
                        null
                )
        )
                .isInstanceOf(InvalidTransactionException.class)
                .hasMessageContaining("Bank ID");
    }

    @Test
    @DisplayName("Debe crear transacción válida con parámetros correctos")
    void testCreateValidTransaction() {
        Transaction tx = Transaction.create(
                "REF123",
                "1234567890",
                "0987654321",
                BigDecimal.valueOf(100),
                "BANK_1"
        );

        assertNotNull(tx);
        assertNotNull(tx.getId());
        assertNotNull(tx.getCreatedAt());
    }
}
