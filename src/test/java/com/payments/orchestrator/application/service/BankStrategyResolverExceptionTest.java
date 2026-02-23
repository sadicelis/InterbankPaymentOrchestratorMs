package com.payments.orchestrator.application.service;

import com.payments.orchestrator.application.exception.BankNotFoundException;
import com.payments.orchestrator.application.exception.BankStrategyResolutionException;
import com.payments.orchestrator.domain.port.BankStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("BankStrategyResolver Exception Tests")
class BankStrategyResolverExceptionTest {

    private BankStrategyResolver resolver;
    private BankStrategy mockStrategy1;
    private BankStrategy mockStrategy2;

    @BeforeEach
    void setUp() {
        mockStrategy1 = mock(BankStrategy.class);
        mockStrategy2 = mock(BankStrategy.class);
    }

    @Test
    @DisplayName("Debe lanzar BankNotFoundException cuando no encuentra estrategia")
    void testResolveWithUnknownBankCode() {
        
        when(mockStrategy1.getBankCode()).thenReturn("BANK_1");
        List<BankStrategy> strategies = Arrays.asList(mockStrategy1);
        resolver = new BankStrategyResolver(strategies);
        
        assertThatThrownBy(() -> resolver.resolve("BANK_UNKNOWN"))
                .isInstanceOf(BankNotFoundException.class)
                .hasMessageContaining("No strategy registered");
    }

    @Test
    @DisplayName("Debe lanzar BankStrategyResolutionException cuando hay duplicados")
    void testConstructorWithDuplicateStrategies() {
        
        when(mockStrategy1.getBankCode()).thenReturn("BANK_1");
        when(mockStrategy2.getBankCode()).thenReturn("BANK_1");
        List<BankStrategy> strategies = Arrays.asList(mockStrategy1, mockStrategy2);
        
        assertThatThrownBy(() -> new BankStrategyResolver(strategies))
                .isInstanceOf(BankStrategyResolutionException.class)
                .hasMessageContaining("Duplicate BankStrategy");
    }

    @Test
    @DisplayName("Debe resolver estrategia correcta si existe")
    void testResolveSuccessfully() {
        
        when(mockStrategy1.getBankCode()).thenReturn("BANK_1");
        List<BankStrategy> strategies = Arrays.asList(mockStrategy1);
        resolver = new BankStrategyResolver(strategies);
        
        BankStrategy result = resolver.resolve("BANK_1");

        assertNotNull(result);
        assert result.getBankCode().equals("BANK_1");
    }

    @Test
    @DisplayName("Debe manejar lista vacía de estrategias")
    void testResolveWithEmptyStrategies() {
        
        List<BankStrategy> strategies = Collections.emptyList();
        resolver = new BankStrategyResolver(strategies);

        assertThatThrownBy(() -> resolver.resolve("BANK_1"))
                .isInstanceOf(BankNotFoundException.class);
    }
}
