package com.payments.orchestrator.infrastructure.adapter.out.bankstrategy;

import com.payments.orchestrator.domain.model.BankTransferResult;
import com.payments.orchestrator.domain.model.aggregate.Transaction;
import com.payments.orchestrator.infrastructure.adapter.out.bankstrategy.mapper.BankRequestMapper;
import com.payments.orchestrator.infrastructure.config.BankClientProperties;
import com.payments.orchestrator.infrastructure.config.constants.BankCodesConstants;
import com.payments.orchestrator.infrastructure.config.constants.BankResponseConstants;
import com.payments.orchestrator.infrastructure.exception.ExternalServiceException;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class Bank2StrategyTest {

    private Bank2Strategy newStrategy(BankClientProperties properties, BankRequestMapper mapper) {
        WebClient.Builder builder = mock(WebClient.Builder.class); 
        return new Bank2Strategy(builder, properties, mapper);
    }

    @Test
    void constructor_when_bank2_config_missing_throws_externalServiceException() {
        BankClientProperties properties = mock(BankClientProperties.class);
        BankRequestMapper mapper = mock(BankRequestMapper.class);

        when(properties.getClients()).thenReturn(Map.of()); 

        ExternalServiceException ex = assertThrows(
                ExternalServiceException.class,
                () -> newStrategy(properties, mapper)
        );

        assertTrue(ex.getMessage().contains("Configuration for BANK_2 not found"));
    }

    @Test
    void getBankCode_returns_BANK_2() {
        BankClientProperties properties = mock(BankClientProperties.class);
        BankClientProperties.Client client = mock(BankClientProperties.Client.class);
        when(properties.getClients()).thenReturn(Map.of(BankCodesConstants.BANK_2, client));

        BankRequestMapper mapper = mock(BankRequestMapper.class);

        Bank2Strategy strategy = newStrategy(properties, mapper);

        assertEquals(BankCodesConstants.BANK_2, strategy.getBankCode());
    }

    @Test
    void sendTransfer_returns_success_result_after_mock_delay() {
        BankClientProperties properties = mock(BankClientProperties.class);
        BankClientProperties.Client client = mock(BankClientProperties.Client.class);
        when(properties.getClients()).thenReturn(Map.of(BankCodesConstants.BANK_2, client));

        BankRequestMapper mapper = mock(BankRequestMapper.class);

        Bank2Strategy strategy = newStrategy(properties, mapper);

        Transaction tx = mock(Transaction.class);

        StepVerifier.create(strategy.sendTransfer(tx))
                .assertNext(res -> assertBank2MockSuccess(res))
                .verifyComplete();
    }

    @Test
    void bankResponseConstants_haveRequiredValues() {
        assertNotNull(BankResponseConstants.BANK_RESPONSE_SUCCESS_CODE);
        assertNotNull(BankResponseConstants.BANK_MSG_SUCCESS);
        assertNotNull(BankResponseConstants.BANK_MOCK_TXN_ID);

        assertEquals("00", BankResponseConstants.BANK_RESPONSE_SUCCESS_CODE);
        assertEquals("Transfer successful", BankResponseConstants.BANK_MSG_SUCCESS);
        assertEquals("txn_123", BankResponseConstants.BANK_MOCK_TXN_ID);
    }

    @Test
    void bankResponseConstants_mockDelay_isValid() {
        assertTrue(BankResponseConstants.BANK_MOCK_DELAY_MS > 0);
        assertEquals(200, BankResponseConstants.BANK_MOCK_DELAY_MS);
    }

    @Test
    void bankResponseConstants_timeoutConfiguration() {
        assertTrue(BankResponseConstants.BANK_REQUEST_TIMEOUT_SECONDS > 0);
        assertEquals(10, BankResponseConstants.BANK_REQUEST_TIMEOUT_SECONDS);
    }

    private static void assertBank2MockSuccess(BankTransferResult res) {
        assertNotNull(res);
        assertTrue(res.isSuccess());
        assertEquals(BankResponseConstants.BANK_RESPONSE_SUCCESS_CODE, res.getCode());
        assertEquals(BankResponseConstants.BANK_MSG_SUCCESS, res.getMessage());
        assertEquals(BankResponseConstants.BANK_MOCK_TXN_ID, res.getExternalRef());
    }
}