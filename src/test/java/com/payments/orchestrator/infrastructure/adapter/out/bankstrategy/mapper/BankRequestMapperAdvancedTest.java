package com.payments.orchestrator.infrastructure.adapter.out.bankstrategy.mapper;

import com.payments.orchestrator.domain.model.aggregate.Transaction;
import com.payments.orchestrator.infrastructure.adapter.out.bankstrategy.dto.BankTransferRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class BankRequestMapperAdvancedTest {

    private BankRequestMapper mapper = new BankRequestMapper();

    @Test
    void shouldMapTransactionToBankRequest() {
        Transaction transaction = Transaction.create(
                "REF001",
                "ACC001",
                "ACC002",
                new BigDecimal("1000.00"),
                "BANK001"
        );

        BankTransferRequest request = mapper.toBankRequest(transaction);

        assertThat(request).isNotNull();
        assertThat(request.getSourceAccount()).isEqualTo("ACC001");
        assertThat(request.getDestinationAccount()).isEqualTo("ACC002");
        assertThat(request.getAmount()).isEqualTo("1000.00");
        assertThat(request.getReference()).isEqualTo("REF001");
    }

    @Test
    void shouldPreserveAllTransactionFields() {
        Transaction transaction = Transaction.create(
                "REF002",
                "SOURCE123",
                "DEST456",
                new BigDecimal("5500.50"),
                "BANK002"
        );

        BankTransferRequest request = mapper.toBankRequest(transaction);

        assertThat(request.getSourceAccount()).isEqualTo("SOURCE123");
        assertThat(request.getDestinationAccount()).isEqualTo("DEST456");
        assertThat(request.getAmount()).isEqualTo("5500.50");
        assertThat(request.getReference()).isEqualTo("REF002");
    }

    @Test
    void shouldHandleSmallAmounts() {
        Transaction transaction = Transaction.create(
                "REF003",
                "ACC001",
                "ACC002",
                new BigDecimal("0.01"),
                "BANK001"
        );

        BankTransferRequest request = mapper.toBankRequest(transaction);

        assertThat(request.getAmount()).isEqualTo("0.01");
    }

    @Test
    void shouldHandleLargeAmounts() {
        Transaction transaction = Transaction.create(
                "REF004",
                "ACC001",
                "ACC002",
                new BigDecimal("9999999.99"),
                "BANK001"
        );

        BankTransferRequest request = mapper.toBankRequest(transaction);

        assertThat(request.getAmount()).isEqualTo("9999999.99");
    }

    @Test
    void shouldHandleSpecialCharactersInReference() {
        Transaction transaction = Transaction.create(
                "REF-@005-&2024",
                "ACC001",
                "ACC002",
                new BigDecimal("500.00"),
                "BANK001"
        );

        BankTransferRequest request = mapper.toBankRequest(transaction);

        assertThat(request.getReference()).isEqualTo("REF-@005-&2024");
    }

    @Test
    void shouldHandleAccountsWithSpecialFormats() {
        Transaction transaction = Transaction.create(
                "REF006",
                "ACC-001-XYZ",
                "ACC/002/ABC",
                new BigDecimal("750.00"),
                "BANK001"
        );

        BankTransferRequest request = mapper.toBankRequest(transaction);

        assertThat(request.getSourceAccount()).isEqualTo("ACC-001-XYZ");
        assertThat(request.getDestinationAccount()).isEqualTo("ACC/002/ABC");
    }

    @Test
    void shouldMapWithVeryLongReferences() {
        String longReference = "REF-" + "x".repeat(100) + "-2024";
        Transaction transaction = Transaction.create(
                longReference,
                "ACC001",
                "ACC002",
                new BigDecimal("100.00"),
                "BANK001"
        );

        BankTransferRequest request = mapper.toBankRequest(transaction);

        assertThat(request.getReference()).isEqualTo(longReference);
    }
}
