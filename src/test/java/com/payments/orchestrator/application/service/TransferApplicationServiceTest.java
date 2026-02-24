package com.payments.orchestrator.application.service;

import com.payments.orchestrator.domain.model.BankTransferResult;
import com.payments.orchestrator.domain.model.aggregate.Transaction;
import com.payments.orchestrator.domain.model.enums.TransactionStatus;
import com.payments.orchestrator.domain.port.BankStrategy;
import com.payments.orchestrator.domain.port.TransferRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransferApplicationServiceTest {

    private BankStrategyResolver resolver;
    private TransferRepositoryPort repository;
    private BankStrategy strategy;
    private TransferApplicationService service;

    @BeforeEach
    void setUp() {
        resolver = mock(BankStrategyResolver.class);
        repository = mock(TransferRepositoryPort.class);
        strategy = mock(BankStrategy.class);

        service = new TransferApplicationService(resolver, repository);
    }

    @Test
    void process_success_shouldPersistPending_callBank_andPersistSuccessful() {
        Transaction tx = Transaction.create("ref-1", "A", "B", new BigDecimal("10.00"), "BANK_1");

        when(repository.savePending(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        when(resolver.resolve("BANK_1")).thenReturn(strategy);

        when(strategy.sendTransfer(any())).thenReturn(Mono.just(
                new BankTransferResult(true, "00", "OK", "EXT-123")
        ));

        when(repository.updateStatus(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(service.process(tx))
                .assertNext(id -> assertEquals(tx.getId(), id))
                .verifyComplete();


        verify(repository, times(1)).savePending(any());
        verify(repository, times(1)).updateStatus(any());

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(repository).updateStatus(captor.capture());

        Transaction updated = captor.getValue();
        assertEquals(TransactionStatus.SUCCESSFUL, updated.getStatus());
        assertNull(updated.getErrorMessage());
    }

    @Test
    void process_businessFailure_shouldPersistFailedWithMessage() {
        Transaction tx = Transaction.create("ref-2", "A", "B", new BigDecimal("10.00"), "BANK_1");

        when(repository.savePending(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        when(resolver.resolve("BANK_1")).thenReturn(strategy);

        when(strategy.sendTransfer(any())).thenReturn(Mono.just(
                new BankTransferResult(false, "51", "Insufficient funds", null)
        ));

        when(repository.updateStatus(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(service.process(tx))
                .assertNext(id -> assertEquals(tx.getId(), id))
                .verifyComplete();

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(repository).updateStatus(captor.capture());

        Transaction updated = captor.getValue();
        assertEquals(TransactionStatus.FAILED, updated.getStatus());
        assertNotNull(updated.getErrorMessage());
        assertTrue(updated.getErrorMessage().contains("51"));
        assertTrue(updated.getErrorMessage().contains("Insufficient"));
    }

    @Test
    void process_exception_shouldMarkFailedAndPersistIt() {
        Transaction tx = Transaction.create("ref-3", "A", "B", new BigDecimal("10.00"), "BANK_1");

        when(repository.savePending(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        when(resolver.resolve("BANK_1")).thenReturn(strategy);

        when(strategy.sendTransfer(any())).thenReturn(Mono.error(new RuntimeException("Bank timeout")));
        when(repository.updateStatus(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(service.process(tx))
                .assertNext(id -> assertEquals(tx.getId(), id))
                .verifyComplete();

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(repository).updateStatus(captor.capture());

        Transaction updated = captor.getValue();
        assertEquals(TransactionStatus.FAILED, updated.getStatus());
        assertNotNull(updated.getErrorMessage());
        assertTrue(updated.getErrorMessage().contains("RuntimeException") || updated.getErrorMessage().contains("timeout"));
    }

    @Test
    void process_withLargeAmount_shouldSucceed() {
        Transaction tx = Transaction.create("ref-large", "A", "B", new BigDecimal("999999.99"), "BANK_1");

        when(repository.savePending(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        when(resolver.resolve("BANK_1")).thenReturn(strategy);
        when(strategy.sendTransfer(any())).thenReturn(Mono.just(
                new BankTransferResult(true, "00", "OK", "EXT-999")
        ));
        when(repository.updateStatus(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(service.process(tx))
                .assertNext(id -> assertEquals(tx.getId(), id))
                .verifyComplete();

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(repository).updateStatus(captor.capture());
        
        Transaction updated = captor.getValue();
        assertEquals(TransactionStatus.SUCCESSFUL, updated.getStatus());
    }

    @Test
    void process_shouldCallBankStrategyWithCorrectTransaction() {
        Transaction tx = Transaction.create("ref-4", "SRC", "DST", new BigDecimal("500.00"), "BANK_2");

        when(repository.savePending(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        when(resolver.resolve("BANK_2")).thenReturn(strategy);
        when(strategy.sendTransfer(any())).thenReturn(Mono.just(
                new BankTransferResult(true, "00", "OK", "TXN-XYZ")
        ));
        when(repository.updateStatus(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(service.process(tx))
                .assertNext(id -> assertEquals(tx.getId(), id))
                .verifyComplete();

        ArgumentCaptor<Transaction> bankCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(strategy).sendTransfer(bankCaptor.capture());
        
        Transaction capturedTx = bankCaptor.getValue();
        assertEquals("SRC", capturedTx.getSourceAccount());
        assertEquals("DST", capturedTx.getDestinationAccount());
        assertEquals(new BigDecimal("500.00"), capturedTx.getAmount());
    }

    @Test
    void process_failure_includesErrorCodeAndMessage() {
        Transaction tx = Transaction.create("ref-fail", "A", "B", new BigDecimal("100.00"), "BANK_1");

        when(repository.savePending(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        when(resolver.resolve("BANK_1")).thenReturn(strategy);
        when(strategy.sendTransfer(any())).thenReturn(Mono.just(
                new BankTransferResult(false, "99", "Generic error", null)
        ));
        when(repository.updateStatus(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(service.process(tx))
                .assertNext(id -> assertEquals(tx.getId(), id))
                .verifyComplete();

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(repository).updateStatus(captor.capture());

        Transaction updated = captor.getValue();
        assertEquals(TransactionStatus.FAILED, updated.getStatus());
        assertTrue(updated.getErrorMessage().contains("99"));
        assertTrue(updated.getErrorMessage().contains("Generic error"));
    }
}