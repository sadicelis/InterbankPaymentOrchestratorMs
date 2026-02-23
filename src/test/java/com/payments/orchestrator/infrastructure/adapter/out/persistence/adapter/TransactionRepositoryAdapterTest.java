package com.payments.orchestrator.infrastructure.adapter.out.persistence.adapter;

import com.payments.orchestrator.domain.model.aggregate.Transaction;
import com.payments.orchestrator.domain.model.enums.TransactionStatus;
import com.payments.orchestrator.infrastructure.adapter.out.persistence.BankJpaRepository;
import com.payments.orchestrator.infrastructure.adapter.out.persistence.TransactionRepository;
import com.payments.orchestrator.infrastructure.adapter.out.persistence.Entities.BankEntity;
import com.payments.orchestrator.infrastructure.adapter.out.persistence.Entities.TransactionEntity;
import com.payments.orchestrator.infrastructure.adapter.out.persistence.mapper.TransactionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionRepositoryAdapterTest {

    @Mock
    private TransactionRepository repository;

    @Mock
    private BankJpaRepository bankRepository;

    private TransactionMapper mapper;

    private TransactionRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        mapper = new TransactionMapper();
        adapter = new TransactionRepositoryAdapter(repository, bankRepository, mapper);
    }

    @Test
    void savePending_shouldFindBank_save_andReturnDomain() {

        Transaction request = Transaction.create(
                "ref-1",
                "A",
                "B",
                new BigDecimal("10.00"),
                "BANK_1"
        );

        BankEntity bank = BankEntity.builder()
                .id(UUID.randomUUID())
                .code("BANK_1")
                .name("Bank 1")
                .active(true)
                .createdAt(LocalDateTime.now().minusDays(10))
                .build();

        when(bankRepository.findByCodeAndActiveTrue("BANK_1")).thenReturn(Optional.of(bank));

        when(repository.save(any(TransactionEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        StepVerifier.create(adapter.savePending(request))
                .assertNext(tx -> {
                    assertEquals(request.getId(), tx.getId());
                    assertEquals(TransactionStatus.PENDING, tx.getStatus());
                    assertEquals("BANK_1", tx.getBankId());
                })
                .verifyComplete();


        verify(bankRepository, times(1)).findByCodeAndActiveTrue("BANK_1");

        ArgumentCaptor<TransactionEntity> entityCaptor = ArgumentCaptor.forClass(TransactionEntity.class);
        verify(repository, times(1)).save(entityCaptor.capture());

        TransactionEntity savedEntity = entityCaptor.getValue();
        assertEquals(request.getId(), savedEntity.getId());
        assertEquals("ref-1", savedEntity.getReference());
        assertEquals("A", savedEntity.getSourceAccount());
        assertEquals("B", savedEntity.getDestinationAccount());
        assertEquals(new BigDecimal("10.00"), savedEntity.getAmount());
        assertEquals(TransactionStatus.PENDING.getStatusMessage(), savedEntity.getStatus());
        assertSame(bank, savedEntity.getBank());
    }

    @Test
    void savePending_shouldErrorIfBankNotFound() {
        Transaction request = Transaction.create(
                "ref-1",
                "A",
                "B",
                new BigDecimal("10.00"),
                "BANK_X"
        );

        when(bankRepository.findByCodeAndActiveTrue("BANK_X")).thenReturn(Optional.empty());

        StepVerifier.create(adapter.savePending(request))
                .expectError(NoSuchElementException.class) 
                .verify();

        verify(bankRepository, times(1)).findByCodeAndActiveTrue("BANK_X");
        verifyNoInteractions(repository);
    }

    @Test
    void updateStatus_shouldFindById_useExistingBank_save_andReturnDomain() {

        UUID id = UUID.randomUUID();

        BankEntity bank = BankEntity.builder()
                .id(UUID.randomUUID())
                .code("BANK_1")
                .name("Bank 1")
                .active(true)
                .createdAt(LocalDateTime.now().minusDays(10))
                .build();

        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);

        TransactionEntity existingEntity = TransactionEntity.builder()
                .id(id)
                .reference("ref-old")
                .sourceAccount("A")
                .destinationAccount("B")
                .amount(new BigDecimal("10.00"))
                .status(TransactionStatus.PENDING.getStatusMessage())
                .bank(bank)
                .errorMessage(null)
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .build();

        Transaction txToUpdate = Transaction.restore(
                id,
                "ref-old",
                "A",
                "B",
                new BigDecimal("10.00"),
                "BANK_1",
                TransactionStatus.FAILED,
                "timeout",
                createdAt,
                LocalDateTime.now()
        );

        when(repository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(repository.save(any(TransactionEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        StepVerifier.create(adapter.updateStatus(txToUpdate))
                .assertNext(updated -> {
                    assertEquals(id, updated.getId());
                    assertEquals(TransactionStatus.FAILED, updated.getStatus());
                    assertEquals("timeout", updated.getErrorMessage());
                    assertEquals("BANK_1", updated.getBankId());
                })
                .verifyComplete();


        verify(repository, times(1)).findById(id);

        ArgumentCaptor<TransactionEntity> entityCaptor = ArgumentCaptor.forClass(TransactionEntity.class);
        verify(repository, times(1)).save(entityCaptor.capture());

        TransactionEntity savedEntity = entityCaptor.getValue();
        assertSame(bank, savedEntity.getBank(), "Debe conservar el bank existente");
        assertEquals(TransactionStatus.FAILED.getStatusMessage(), savedEntity.getStatus());
        assertEquals("timeout", savedEntity.getErrorMessage());

        assertEquals(createdAt, savedEntity.getCreatedAt(), "createdAt no debe cambiar");
    }

    @Test
    void updateStatus_shouldErrorIfTransactionNotFound() {
        UUID id = UUID.randomUUID();

        Transaction txToUpdate = Transaction.restore(
                id,
                "ref",
                "A",
                "B",
                new BigDecimal("1.00"),
                "BANK_1",
                TransactionStatus.SUCCESSFUL,
                null,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now()
        );

        when(repository.findById(id)).thenReturn(Optional.empty());

        StepVerifier.create(adapter.updateStatus(txToUpdate))
                .expectErrorSatisfies(ex -> {
                    assertTrue(ex instanceof IllegalStateException);
                    assertTrue(ex.getMessage().contains("Transaction not found"));
                })
                .verify();

        verify(repository, times(1)).findById(id);
        verify(repository, never()).save(any());
    }
}