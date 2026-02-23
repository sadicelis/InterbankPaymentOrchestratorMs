package com.payments.orchestrator.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WebClientFactoryTest {

    @Test
    void create_shouldBuildWebClientWithBaseUrlFromProperties() {
      
        WebClient.Builder builder = mock(WebClient.Builder.class);
        BankClientProperties properties = mock(BankClientProperties.class);

        BankClientProperties.Client clientCfg = mock(BankClientProperties.Client.class);
        when(clientCfg.getBaseUrl()).thenReturn("http://bank1-mock:8081");

        when(properties.getClients()).thenReturn(Map.of("BANK_1", clientCfg));

        WebClient builtClient = mock(WebClient.class);

        when(builder.baseUrl("http://bank1-mock:8081")).thenReturn(builder);
        when(builder.build()).thenReturn(builtClient);

        WebClientFactory factory = new WebClientFactory(builder, properties);

        WebClient result = factory.create("BANK_1");

        assertSame(builtClient, result);

        verify(properties).getClients();
        verify(builder).baseUrl("http://bank1-mock:8081");
        verify(builder).build();
    }

    @Test
    void create_shouldThrowIfBankCodeNotConfigured() {
        WebClient.Builder builder = mock(WebClient.Builder.class);
        BankClientProperties properties = mock(BankClientProperties.class);

        when(properties.getClients()).thenReturn(Map.of()); 

        WebClientFactory factory = new WebClientFactory(builder, properties);

        assertThrows(NullPointerException.class, () -> factory.create("BANK_X"));
        verify(properties).getClients();
        verifyNoInteractions(builder);
    }
}