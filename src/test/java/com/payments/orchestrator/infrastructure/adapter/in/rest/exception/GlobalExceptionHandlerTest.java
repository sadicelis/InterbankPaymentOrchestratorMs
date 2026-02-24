package com.payments.orchestrator.infrastructure.adapter.in.rest.exception;

import com.payments.orchestrator.application.exception.BankNotFoundException;
import com.payments.orchestrator.application.exception.BankStrategyResolutionException;
import com.payments.orchestrator.domain.exception.InvalidTransactionException;
import com.payments.orchestrator.infrastructure.adapter.in.rest.dto.ApiResponse;
import com.payments.orchestrator.infrastructure.exception.BankCommunicationException;
import com.payments.orchestrator.infrastructure.exception.PersistenceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private WebRequest mockRequest;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        mockRequest = mock(WebRequest.class);
        when(mockRequest.getDescription(false)).thenReturn("uri=/api/v1/transfers");
    }

    @Test
    @DisplayName("Debe manejar InvalidTransactionException y retornar 400")
    void testHandleInvalidTransactionException() {

        InvalidTransactionException ex = new InvalidTransactionException("Invalid amount");

        ResponseEntity<ApiResponse<?>> response = handler.handleInvalidTransactionException(ex, mockRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo("ERROR");
        assertThat(response.getBody().getCode()).isEqualTo("ERR_INVALID_TRANSACTION");
    }

    @Test
    @DisplayName("Debe manejar BankNotFoundException y retornar 404")
    void testHandleBankNotFoundException() {

        BankNotFoundException ex = new BankNotFoundException("Bank not found");

        ResponseEntity<ApiResponse<?>> response = handler.handleBankNotFoundException(ex, mockRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("ERR_BANK_NOT_FOUND");
    }

    @Test
    @DisplayName("Debe manejar BankStrategyResolutionException y retornar 422")
    void testHandleBankStrategyResolutionException() {

        BankStrategyResolutionException ex = new BankStrategyResolutionException("Duplicate strategy");

        ResponseEntity<ApiResponse<?>> response = handler.handleBankStrategyResolutionException(ex, mockRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("ERR_STRATEGY_RESOLUTION");
    }

    @Test
    @DisplayName("Debe manejar BankCommunicationException y retornar 503")
    void testHandleBankCommunicationException() {

        BankCommunicationException ex = new BankCommunicationException("Bank unavailable");

        ResponseEntity<ApiResponse<?>> response = handler.handleBankCommunicationException(ex, mockRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("ERR_BANK_COMMUNICATION");
    }

    @Test
    @DisplayName("Debe manejar PersistenceException y retornar 500")
    void testHandlePersistenceException() {

        PersistenceException ex = new PersistenceException("Database error");

        ResponseEntity<ApiResponse<?>> response = handler.handlePersistenceException(ex, mockRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("ERR_PERSISTENCE");
    }

    @Test
    @DisplayName("Debe manejar TimeoutException y retornar 504")
    void testHandleTimeoutException() {

        java.util.concurrent.TimeoutException ex = new java.util.concurrent.TimeoutException("Request timeout");

        ResponseEntity<ApiResponse<?>> response = handler.handleTimeoutException(ex, mockRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.GATEWAY_TIMEOUT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("ERR_TIMEOUT");
    }

    @Test
    @DisplayName("Debe manejar Exception genérica y retornar 500")
    void testHandleGenericException() {

        Exception ex = new Exception("Unexpected error");

        ResponseEntity<ApiResponse<?>> response = handler.handleGenericException(ex, mockRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("ERR_INTERNAL_SERVER_ERROR");
    }

    @Test
    @DisplayName("Debe manejar request sin URI")
    void testRequestWithoutUri() {
        InvalidTransactionException ex = new InvalidTransactionException("Invalid");
        when(mockRequest.getDescription(false)).thenReturn("");

        ResponseEntity<ApiResponse<?>> response = handler.handleInvalidTransactionException(ex, mockRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("Debe setear correctamente el timestamp en la respuesta")
    void testResponseHasTimestamp() {
        InvalidTransactionException ex = new InvalidTransactionException("Invalid");

        ResponseEntity<ApiResponse<?>> response = handler.handleInvalidTransactionException(ex, mockRequest);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("Debe clasificar errores HTTP 5xx correctamente")
    void testServerErrorClassification() {
        BankCommunicationException ex = new BankCommunicationException("Server error 503");

        ResponseEntity<ApiResponse<?>> response = handler.handleBankCommunicationException(ex, mockRequest);

        assertThat(response.getStatusCode().value()).isGreaterThanOrEqualTo(400);
    }

    @Test
    @DisplayName("BankCommunicationException debe retornar 503")
    void testBankCommunicationExceptionStatus() {
        BankCommunicationException ex = new BankCommunicationException("Bank service down");

        ResponseEntity<ApiResponse<?>> response = handler.handleBankCommunicationException(ex, mockRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody().getCode()).isEqualTo("ERR_BANK_COMMUNICATION");
    }

    @Test
    @DisplayName("PersistenceException debe retornar 500 (Internal Server Error)")
    void testPersistenceExceptionDetail() {
        PersistenceException ex = new PersistenceException("Cannot connect to database");

        ResponseEntity<ApiResponse<?>> response = handler.handlePersistenceException(ex, mockRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isNull();
    }

    @Test
    @DisplayName("TimeoutException debe retornar 504 (Gateway Timeout)")
    void testTimeoutExceptionDetail() {
        java.util.concurrent.TimeoutException ex = new java.util.concurrent.TimeoutException("Operation exceeded timeout");

        ResponseEntity<ApiResponse<?>> response = handler.handleTimeoutException(ex, mockRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.GATEWAY_TIMEOUT);
        assertThat(response.getBody().getCode()).isEqualTo("ERR_TIMEOUT");
    }

    @Test
    @DisplayName("BankStrategyResolutionException retorna 422 (Unprocessable Entity)")
    void testBankStrategyResolutionExceptionStatus() {
        BankStrategyResolutionException ex = new BankStrategyResolutionException("Multiple strategies configured");

        ResponseEntity<ApiResponse<?>> response = handler.handleBankStrategyResolutionException(ex, mockRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody().getCode()).isEqualTo("ERR_STRATEGY_RESOLUTION");
    }
}
