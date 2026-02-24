package com.payments.orchestrator.application.service;

import com.payments.orchestrator.application.exception.ApplicationException;
import com.payments.orchestrator.application.exception.TransferProcessingException;
import com.payments.orchestrator.domain.exception.DomainException;
import com.payments.orchestrator.domain.model.BankTransferResult;
import com.payments.orchestrator.domain.model.aggregate.Transaction;
import com.payments.orchestrator.domain.model.enums.TransactionStatus;
import com.payments.orchestrator.domain.port.BankStrategy;
import com.payments.orchestrator.domain.port.TransferRepositoryPort;
import com.payments.orchestrator.infrastructure.exception.InfrastructureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
        when(strategy.sendTransfer(any())).thenReturn(Mono.just(new BankTransferResult(true, "00", "OK", "EXT-123")));
        when(repository.updateStatus(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(service.process(tx))
                .assertNext(id -> assertEquals(tx.getId(), id))
                .verifyComplete();

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
        when(strategy.sendTransfer(any())).thenReturn(Mono.just(new BankTransferResult(false, "51", "Insufficient funds", null)));
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
        assertTrue(updated.getErrorMessage().toLowerCase().contains("insufficient"));
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
        assertTrue(updated.getErrorMessage().contains("RuntimeException") || updated.getErrorMessage().toLowerCase().contains("timeout"));
    }

    @Test
    void process_timeoutException_shouldPersistFailed_andReturnId() {
        Transaction tx = Transaction.create("ref-timeout", "A", "B", new BigDecimal("10.00"), "BANK_1");

        when(repository.savePending(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        when(resolver.resolve("BANK_1")).thenReturn(strategy);
        when(strategy.sendTransfer(any())).thenReturn(Mono.error(new TimeoutException("Bank timeout")));
        when(repository.updateStatus(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(service.process(tx))
                .expectNext(tx.getId())
                .verifyComplete();

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(repository, atLeastOnce()).updateStatus(captor.capture());

        Transaction updated = captor.getValue();
        assertEquals(TransactionStatus.FAILED, updated.getStatus());
        assertNotNull(updated.getErrorMessage());
        assertTrue(updated.getErrorMessage().toLowerCase().contains("timeout") || updated.getErrorMessage().toLowerCase().contains("time"));
    }

    @Test
    void process_withSmallAmount_shouldSucceed() {
        Transaction tx = Transaction.create("ref-small", "A", "B", new BigDecimal("0.01"), "BANK_1");

        when(repository.savePending(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        when(resolver.resolve("BANK_1")).thenReturn(strategy);
        when(strategy.sendTransfer(any())).thenReturn(Mono.just(new BankTransferResult(true, "00", "OK", "EXT-001")));
        when(repository.updateStatus(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(service.process(tx))
                .expectNext(tx.getId())
                .verifyComplete();

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(repository, times(1)).updateStatus(captor.capture());

        Transaction updated = captor.getValue();
        assertEquals(TransactionStatus.SUCCESSFUL, updated.getStatus());
        assertNull(updated.getErrorMessage());
    }

    @Test
    void process_shouldCallBankStrategyWithCorrectTransaction() {
        Transaction tx = Transaction.create("ref-4", "SRC", "DST", new BigDecimal("500.00"), "BANK_2");

        when(repository.savePending(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        when(resolver.resolve("BANK_2")).thenReturn(strategy);
        when(strategy.sendTransfer(any())).thenReturn(Mono.just(new BankTransferResult(true, "00", "OK", "TXN-XYZ")));
        when(repository.updateStatus(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(service.process(tx))
                .expectNext(tx.getId())
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
        when(strategy.sendTransfer(any())).thenReturn(Mono.just(new BankTransferResult(false, "99", "Generic error", null)));
        when(repository.updateStatus(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(service.process(tx))
                .expectNext(tx.getId())
                .verifyComplete();

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(repository).updateStatus(captor.capture());

        Transaction updated = captor.getValue();
        assertEquals(TransactionStatus.FAILED, updated.getStatus());
        assertTrue(updated.getErrorMessage().contains("99"));
        assertTrue(updated.getErrorMessage().toLowerCase().contains("generic"));
    }

    // ==========================================================
    // NUEVOS tests para cubrir el catch del resolver + onErrorMap
    // ==========================================================

    static class TestAppException extends ApplicationException {
        public TestAppException(String message, String code) { super(message, code); }
    }

    @Test
    void process_whenResolverThrowsApplicationException_shouldPersistFailed_andPropagateSameException() {
        Transaction tx = Transaction.create("ref-resolver-app", "A", "B", new BigDecimal("10.00"), "BANK_1");

        when(repository.savePending(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        when(repository.updateStatus(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        when(resolver.resolve("BANK_1")).thenThrow(new TestAppException("resolver fail", "ERR_APP"));

        StepVerifier.create(service.process(tx))
                .expectErrorSatisfies(err -> {
                    assertTrue(err instanceof TestAppException);
                    assertEquals("resolver fail", err.getMessage());
                })
                .verify();

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(repository).updateStatus(captor.capture());

        Transaction updated = captor.getValue();
        assertEquals(TransactionStatus.FAILED, updated.getStatus());
        assertNotNull(updated.getErrorMessage());
        assertTrue(updated.getErrorMessage().contains("TestAppException"));
    }

    @Test
    void process_whenResolverThrowsRuntimeException_shouldPersistFailed_andMapToTransferProcessingException() {
        Transaction tx = Transaction.create("ref-resolver-rt", "A", "B", new BigDecimal("10.00"), "BANK_1");

        when(repository.savePending(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        when(repository.updateStatus(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        when(resolver.resolve("BANK_1")).thenThrow(new IllegalStateException("boom"));

        StepVerifier.create(service.process(tx))
                .expectErrorSatisfies(err -> {
                    assertTrue(err instanceof TransferProcessingException);
                    assertTrue(err.getMessage().contains("Unexpected error"));
                    assertNotNull(err.getCause());
                    assertTrue(err.getCause() instanceof IllegalStateException);
                })
                .verify();

        verify(repository, times(1)).updateStatus(any());
    }

    // ==========================================================
    // Cobertura directa de fallback + ramas de helpers privados
    // ==========================================================

    @Test
    void handleCircuitBreakerFallback_shouldReturnTransferProcessingException() {
        Transaction tx = Transaction.create("ref-cb", "A", "B", new BigDecimal("10.00"), "BANK_1");

        StepVerifier.create(service.handleCircuitBreakerFallback(tx, new RuntimeException("CircuitBreaker OPEN")))
                .expectErrorSatisfies(err -> {
                    assertTrue(err instanceof TransferProcessingException);
                    assertTrue(err.getMessage().toLowerCase().contains("temporarily unavailable"));
                })
                .verify();
    }

    // ---- reflection helpers to hit private methods & branches ----
    private Object invokePrivate(String methodName, Object... args) {
        Method m = Arrays.stream(TransferApplicationService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals(methodName))
                .filter(mm -> mm.getParameterCount() == args.length)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No se encontró método: " + methodName));
        try {
            m.setAccessible(true);
            return m.invoke(service, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void buildFailureMessage_shouldCoverBranches_nulls_and_values() {
        BankTransferResult r1 = new BankTransferResult(false, null, null, null);
        Object msg1 = invokePrivate("buildFailureMessage", r1);
        assertNotNull(msg1);

        BankTransferResult r2 = new BankTransferResult(false, "51", "Insufficient funds", null);
        Object msg2 = invokePrivate("buildFailureMessage", r2);
        assertNotNull(msg2);

        assertTrue(msg2.toString().contains("51"));
        assertTrue(msg2.toString().toLowerCase().contains("insufficient"));
    }

    @Test
    void normalizeError_shouldCoverBranches_nullMessage_and_nonNullMessage() {
        Object n1 = invokePrivate("normalizeError", new RuntimeException((String) null));
        Object n2 = invokePrivate("normalizeError", new RuntimeException("boom"));

        assertNotNull(n1);
        assertNotNull(n2);

        assertTrue(n2.toString().contains("RuntimeException"));
        // cuando message es null, debe usar DEFAULT_NOT_AVAILABLE (no sabemos el valor exacto, pero sí que no queda "null")
        assertFalse(n1.toString().endsWith(": null"));
    }
}