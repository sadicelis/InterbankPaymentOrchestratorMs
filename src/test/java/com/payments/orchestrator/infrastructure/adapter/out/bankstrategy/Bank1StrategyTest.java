package com.payments.orchestrator.infrastructure.adapter.out.bankstrategy;

import com.payments.orchestrator.domain.model.aggregate.Transaction;
import com.payments.orchestrator.infrastructure.adapter.out.bankstrategy.dto.Bank1ResponseDto;
import com.payments.orchestrator.infrastructure.adapter.out.bankstrategy.dto.BankTransferRequest;
import com.payments.orchestrator.infrastructure.adapter.out.bankstrategy.mapper.BankRequestMapper;
import com.payments.orchestrator.infrastructure.config.BankClientProperties;
import com.payments.orchestrator.infrastructure.config.constants.ApiPathConstants;
import com.payments.orchestrator.infrastructure.config.constants.BankCodesConstants;
import com.payments.orchestrator.infrastructure.config.constants.BankResponseConstants;
import com.payments.orchestrator.infrastructure.exception.ExternalServiceException;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class Bank1StrategyTest {

    private Bank1Strategy newStrategy(WebClient.Builder builder,
                                     WebClient webClient,
                                     BankClientProperties properties,
                                     BankRequestMapper mapper,
                                     String baseUrl) {

        BankClientProperties.Client client = mock(BankClientProperties.Client.class);
        when(client.getBaseUrl()).thenReturn(baseUrl);

        when(properties.getClients()).thenReturn(Map.of(BankCodesConstants.BANK_1, client));
        when(builder.baseUrl(baseUrl)).thenReturn(builder);
        when(builder.build()).thenReturn(webClient);

        return new Bank1Strategy(builder, properties, mapper);
    }

    @Test
    void constructor_when_bank1_config_missing_throws_externalServiceException() {
        WebClient.Builder builder = mock(WebClient.Builder.class);
        BankClientProperties properties = mock(BankClientProperties.class);
        BankRequestMapper mapper = mock(BankRequestMapper.class);

        when(properties.getClients()).thenReturn(Map.of()); 

        ExternalServiceException ex = assertThrows(
                ExternalServiceException.class,
                () -> new Bank1Strategy(builder, properties, mapper)
        );

        assertTrue(ex.getMessage().contains("Configuration for BANK_1 not found"));
        verifyNoInteractions(builder);
    }

    @Test
    void getBankCode_returns_BANK_1() {
        WebClient.Builder builder = mock(WebClient.Builder.class);
        WebClient webClient = mock(WebClient.class);
        BankClientProperties properties = mock(BankClientProperties.class);
        BankRequestMapper mapper = mock(BankRequestMapper.class);

        Bank1Strategy strategy = newStrategy(builder, webClient, properties, mapper, "http://bank1.test");

        assertEquals(BankCodesConstants.BANK_1, strategy.getBankCode());
    }

    @Test
    void sendTransfer_success_maps_to_success_result() {
        WebClient.Builder builder = mock(WebClient.Builder.class);
        WebClient webClient = mock(WebClient.class);

        WebClient.RequestBodyUriSpec postSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec bodySpec = mock(WebClient.RequestBodySpec.class);
        @SuppressWarnings("rawtypes")
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class); // raw para CAP# errors
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        BankClientProperties properties = mock(BankClientProperties.class);
        BankRequestMapper mapper = mock(BankRequestMapper.class);

        Bank1Strategy strategy = newStrategy(builder, webClient, properties, mapper, "http://bank1.test");

        Transaction tx = mock(Transaction.class);
        BankTransferRequest bankRequestBody = mock(BankTransferRequest.class);
        when(mapper.toBankRequest(tx)).thenReturn(bankRequestBody);

        Bank1ResponseDto dto = mock(Bank1ResponseDto.class);
        when(dto.getStatus()).thenReturn(BankResponseConstants.BANK_STATUS_SUCCESS);
        when(dto.getCode()).thenReturn("00");
        when(dto.getMessage()).thenReturn("OK");
        when(dto.getTransactionId()).thenReturn("ext-123");

        when(webClient.post()).thenReturn(postSpec);
        when(postSpec.uri(ApiPathConstants.BANK_TRANSFER_PATH)).thenReturn(bodySpec);
        when(bodySpec.bodyValue(bankRequestBody)).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Bank1ResponseDto.class)).thenReturn(Mono.just(dto));

        StepVerifier.create(strategy.sendTransfer(tx))
                .assertNext(res -> {
                    assertTrue(res.isSuccess());
                    assertEquals("00", res.getCode());
                    assertEquals("OK", res.getMessage());
                    assertEquals("ext-123", res.getExternalRef());
                })
                .verifyComplete();
    }

    @Test
    void sendTransfer_failure_maps_to_failure_result() {
        WebClient.Builder builder = mock(WebClient.Builder.class);
        WebClient webClient = mock(WebClient.class);

        WebClient.RequestBodyUriSpec postSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec bodySpec = mock(WebClient.RequestBodySpec.class);
        @SuppressWarnings("rawtypes")
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        BankClientProperties properties = mock(BankClientProperties.class);
        BankRequestMapper mapper = mock(BankRequestMapper.class);

        Bank1Strategy strategy = newStrategy(builder, webClient, properties, mapper, "http://bank1.test");

        Transaction tx = mock(Transaction.class);
        BankTransferRequest bankRequestBody = mock(BankTransferRequest.class);
        when(mapper.toBankRequest(tx)).thenReturn(bankRequestBody);

        Bank1ResponseDto dto = mock(Bank1ResponseDto.class);
        when(dto.getStatus()).thenReturn("ERROR");
        when(dto.getCode()).thenReturn("51");
        when(dto.getMessage()).thenReturn("Insufficient funds");
        when(dto.getTransactionId()).thenReturn(null);

        when(webClient.post()).thenReturn(postSpec);
        when(postSpec.uri(ApiPathConstants.BANK_TRANSFER_PATH)).thenReturn(bodySpec);
        when(bodySpec.bodyValue(bankRequestBody)).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Bank1ResponseDto.class)).thenReturn(Mono.just(dto));

        StepVerifier.create(strategy.sendTransfer(tx))
                .assertNext(res -> {
                    assertFalse(res.isSuccess());
                    assertEquals("51", res.getCode());
                    assertEquals("Insufficient funds", res.getMessage());
                    assertNull(res.getExternalRef());
                })
                .verifyComplete();
    }
}