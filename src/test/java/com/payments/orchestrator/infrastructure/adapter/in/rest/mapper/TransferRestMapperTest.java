package com.payments.orchestrator.infrastructure.adapter.in.rest.mapper;

import com.payments.orchestrator.domain.model.aggregate.Transaction;
import com.payments.orchestrator.infrastructure.adapter.in.rest.dto.TransferRequestDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class TransferRestMapperTest {

    private TransferRestMapper mapper = new TransferRestMapper();

    @Test
    void shouldMapRequestDtoToTransaction() {
        TransferRequestDto dto = new TransferRequestDto();
        dto.setReference("REF001");
        dto.setSourceAccount("ACC001");
        dto.setDestinationAccount("ACC002");
        dto.setBankCode("BANK001");
        dto.setAmount(new BigDecimal("1000.00"));

        Transaction transaction = mapper.toDomain(dto);

        assertThat(transaction).isNotNull();
        assertThat(transaction.getSourceAccount()).isEqualTo("ACC001");
        assertThat(transaction.getDestinationAccount()).isEqualTo("ACC002");
        assertThat(transaction.getBankId()).isEqualTo("BANK001");
        assertThat(transaction.getAmount()).isEqualTo(new BigDecimal("1000.00"));
        assertThat(transaction.getReference()).isEqualTo("REF001");
    }

    @Test
    void shouldPreserveAllDtoFields() {
        TransferRequestDto dto = new TransferRequestDto();
        dto.setReference("REF002");
        dto.setSourceAccount("SOURCE123");
        dto.setDestinationAccount("DEST456");
        dto.setBankCode("BANK002");
        dto.setAmount(new BigDecimal("5500.50"));

        Transaction transaction = mapper.toDomain(dto);

        assertThat(transaction.getSourceAccount()).isEqualTo("SOURCE123");
        assertThat(transaction.getDestinationAccount()).isEqualTo("DEST456");
        assertThat(transaction.getBankId()).isEqualTo("BANK002");
        assertThat(transaction.getAmount()).isEqualTo(new BigDecimal("5500.50"));
        assertThat(transaction.getReference()).isEqualTo("REF002");
    }

    @Test
    void shouldMapMinimalAmount() {
        TransferRequestDto dto = new TransferRequestDto();
        dto.setReference("REF003");
        dto.setSourceAccount("ACC001");
        dto.setDestinationAccount("ACC002");
        dto.setBankCode("BANK001");
        dto.setAmount(new BigDecimal("0.01"));

        Transaction transaction = mapper.toDomain(dto);

        assertThat(transaction.getAmount()).isEqualTo(new BigDecimal("0.01"));
    }

    @Test
    void shouldMapMaximalAmount() {
        TransferRequestDto dto = new TransferRequestDto();
        dto.setReference("REF004");
        dto.setSourceAccount("ACC001");
        dto.setDestinationAccount("ACC002");
        dto.setBankCode("BANK001");
        dto.setAmount(new BigDecimal("9999999.99"));

        Transaction transaction = mapper.toDomain(dto);

        assertThat(transaction.getAmount()).isEqualTo(new BigDecimal("9999999.99"));
    }

    @Test
    void shouldCreateTransactionWithValidStatus() {
        TransferRequestDto dto = new TransferRequestDto();
        dto.setReference("REF005");
        dto.setSourceAccount("ACC001");
        dto.setDestinationAccount("ACC002");
        dto.setBankCode("BANK001");
        dto.setAmount(new BigDecimal("100.00"));

        Transaction transaction = mapper.toDomain(dto);

        assertThat(transaction.getId()).isNotNull();
        assertThat(transaction.getStatus()).isNotNull();
    }

    @Test
    void shouldMapDtoWithSpecialCharactersInReference() {
        TransferRequestDto dto = new TransferRequestDto();
        dto.setReference("REF-@006-&2024#INV");
        dto.setSourceAccount("ACC001");
        dto.setDestinationAccount("ACC002");
        dto.setBankCode("BANK001");
        dto.setAmount(new BigDecimal("500.00"));

        Transaction transaction = mapper.toDomain(dto);

        assertThat(transaction.getReference()).isEqualTo("REF-@006-&2024#INV");
    }

    @Test
    void shouldMapMultipleDuplicateRequests() {
        for (int i = 0; i < 5; i++) {
            TransferRequestDto dto = new TransferRequestDto();
            dto.setReference("REF" + String.format("%03d", i));
            dto.setSourceAccount("ACC" + String.format("%03d", i));
            dto.setDestinationAccount("ACC" + String.format("%03d", i + 1));
            dto.setBankCode("BANK001");
            dto.setAmount(new BigDecimal(100.00 * (i + 1)));

            Transaction transaction = mapper.toDomain(dto);

            assertThat(transaction).isNotNull();
            assertThat(transaction.getId()).isNotNull();
            assertThat(transaction.getCreatedAt()).isNotNull();
        }
    }
}
