package com.payments.orchestrator.application.service;

import com.payments.orchestrator.domain.port.BankStrategy;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BankStrategyResolverTest {

    @Test
    void resolve_shouldReturnMatchingStrategy() {
        BankStrategy s1 = mock(BankStrategy.class);
        when(s1.getBankCode()).thenReturn("BANK_1");

        BankStrategy s2 = mock(BankStrategy.class);
        when(s2.getBankCode()).thenReturn("BANK_2");

        BankStrategyResolver resolver = new BankStrategyResolver(List.of(s1, s2));

        BankStrategy found = resolver.resolve("BANK_2");
        assertSame(s2, found);
    }

    @Test
    void resolve_shouldThrowIfNotFound() {
        BankStrategy s1 = mock(BankStrategy.class);
        when(s1.getBankCode()).thenReturn("BANK_1");

        BankStrategyResolver resolver = new BankStrategyResolver(List.of(s1));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> resolver.resolve("BANK_X")
        );

        assertTrue(ex.getMessage().contains("BANK_X"));
    }

    @Test
    void constructor_shouldThrowOnDuplicateBankCode() {
        BankStrategy s1 = mock(BankStrategy.class);
        when(s1.getBankCode()).thenReturn("BANK_1");

        BankStrategy s2 = mock(BankStrategy.class);
        when(s2.getBankCode()).thenReturn("BANK_1");

        assertThrows(IllegalStateException.class, () -> new BankStrategyResolver(List.of(s1, s2)));
    }
}