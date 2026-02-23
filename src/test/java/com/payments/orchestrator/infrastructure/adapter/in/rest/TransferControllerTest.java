package com.payments.orchestrator.infrastructure.adapter.in.rest;

import com.payments.orchestrator.application.service.TransferApplicationService;
import com.payments.orchestrator.domain.model.aggregate.Transaction;
import com.payments.orchestrator.infrastructure.adapter.in.rest.dto.TransferRequestDto;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebFluxTest(controllers = TransferController.class)
class TransferControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private TransferApplicationService service;

    @Test
    void transfer_shouldReturn200_andUuidBody() {
        UUID generatedId = UUID.randomUUID();
        when(service.process(any(Transaction.class))).thenReturn(Mono.just(generatedId));

        TransferRequestDto req = new TransferRequestDto();
        req.setReference("ref-001");
        req.setSourceAccount("1001");
        req.setDestinationAccount("2002");
        req.setAmount(new BigDecimal("10.00"));
        req.setBankCode("BANK_1");

        webTestClient.post()
                .uri("/api/v1/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.status").isEqualTo("SUCCESS")
                .jsonPath("$.data.transactionId").isEqualTo(generatedId.toString());

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(service, times(1)).process(captor.capture());

        Transaction tx = captor.getValue();
        assertNotNull(tx.getId());
        assertEquals("ref-001", tx.getReference());
        assertEquals("1001", tx.getSourceAccount());
        assertEquals("2002", tx.getDestinationAccount());
        assertEquals(new BigDecimal("10.00"), tx.getAmount());
        assertEquals("BANK_1", tx.getBankId());
    }

    @Test
    void transfer_shouldReturn400_whenValidationFails() {
        TransferRequestDto req = new TransferRequestDto();
        req.setReference(" ");
        req.setSourceAccount("");
        req.setDestinationAccount(null);
        req.setAmount(null);
        req.setBankCode(" ");

        webTestClient.post()
                .uri("/api/v1/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isBadRequest();

        verifyNoInteractions(service);
    }
}