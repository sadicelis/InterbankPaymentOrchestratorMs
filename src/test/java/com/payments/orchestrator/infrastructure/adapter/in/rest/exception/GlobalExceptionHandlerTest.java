package com.payments.orchestrator.infrastructure.adapter.in.rest.exception;

import com.payments.orchestrator.application.exception.ApplicationException;
import com.payments.orchestrator.application.exception.BankNotFoundException;
import com.payments.orchestrator.application.exception.BankStrategyResolutionException;
import com.payments.orchestrator.domain.exception.DomainException;
import com.payments.orchestrator.domain.exception.InvalidTransactionException;
import com.payments.orchestrator.infrastructure.adapter.in.rest.dto.ApiResponse;
import com.payments.orchestrator.infrastructure.adapter.in.rest.dto.ErrorDetails;
import com.payments.orchestrator.infrastructure.config.constants.BankResponseConstants;
import com.payments.orchestrator.infrastructure.config.constants.DefaultValuesConstants;
import com.payments.orchestrator.infrastructure.config.constants.ErrorCodesConstants;
import com.payments.orchestrator.infrastructure.config.constants.ErrorMessagesConstants;
import com.payments.orchestrator.infrastructure.exception.BankCommunicationException;
import com.payments.orchestrator.infrastructure.exception.ExternalServiceException;
import com.payments.orchestrator.infrastructure.exception.InfrastructureException;
import com.payments.orchestrator.infrastructure.exception.PersistenceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private WebRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("uri=/api/v1/transfers");
    }


    @SuppressWarnings("unused")
    private void dummyControllerMethod(Object body) {
    }

    private MethodParameter dummyMethodParameter() {
        try {
            Method m = this.getClass().getDeclaredMethod("dummyControllerMethod", Object.class);
            return new MethodParameter(m, 0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void assertBasic(ApiResponse<?> body, String expectedCode, String expectedMessage) {
        assertThat(body).isNotNull();
        assertThat(body.getCode()).isEqualTo(expectedCode);
        assertThat(body.getMessage()).isEqualTo(expectedMessage);
        assertThat(body.getTimestamp()).isNotNull();
        assertThat(body.getError()).isNotNull();
        assertThat(body.getError().getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("Debe manejar InvalidTransactionException y retornar 400")
    void handleInvalidTransactionException_returns400() {
        InvalidTransactionException ex = new InvalidTransactionException("Invalid amount");

        ResponseEntity<ApiResponse<?>> response = handler.handleInvalidTransactionException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        ApiResponse<?> body = response.getBody();
        assertBasic(body, ex.getCode(), ErrorMessagesConstants.MSG_INVALID_TRANSACTION);

        ErrorDetails err = body.getError();
        assertThat(err.getPath()).isEqualTo("/api/v1/transfers");
        assertThat(err.getDetail()).isEqualTo("Invalid amount");
        assertThat(err.getExceptionType()).isEqualTo("InvalidTransactionException");
    }

    @Test
    @DisplayName("Debe extraer correctamente la URI del request (se valida en error.path)")
    void uriExtraction_isInErrorPath() {
        InvalidTransactionException ex = new InvalidTransactionException("Invalid");
        when(request.getDescription(false)).thenReturn("uri=/api/v1/transfers/123");

        ResponseEntity<ApiResponse<?>> response = handler.handleInvalidTransactionException(ex, request);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isNotNull();
        assertThat(response.getBody().getError().getPath()).isEqualTo("/api/v1/transfers/123");
    }

    @Test
    @DisplayName("Debe incluir el mensaje de excepción en error.detail (no en message)")
    void exceptionMessage_isInErrorDetail() {
        InvalidTransactionException ex = new InvalidTransactionException("Specific validation error");

        ResponseEntity<ApiResponse<?>> response = handler.handleInvalidTransactionException(ex, request);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo(ErrorMessagesConstants.MSG_INVALID_TRANSACTION);
        assertThat(response.getBody().getError()).isNotNull();
        assertThat(response.getBody().getError().getDetail()).contains("Specific validation error");
    }

    @Test
    @DisplayName("extractPath: description vacío (no empieza con uri=) retorna ''")
    void extractPath_emptyString() {
        InvalidTransactionException ex = new InvalidTransactionException("Invalid");
        when(request.getDescription(false)).thenReturn("");

        ResponseEntity<ApiResponse<?>> response = handler.handleInvalidTransactionException(ex, request);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isNotNull();
        assertThat(response.getBody().getError().getPath()).isEqualTo("");
    }

    @Test
    @DisplayName("extractPath: description null retorna null")
    void extractPath_null() {
        InvalidTransactionException ex = new InvalidTransactionException("Invalid");
        when(request.getDescription(false)).thenReturn(null);

        ResponseEntity<ApiResponse<?>> response = handler.handleInvalidTransactionException(ex, request);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isNotNull();
        assertThat(response.getBody().getError().getPath()).isNull();
    }

    @Test
    @DisplayName("extractPath: description sin prefix uri= retorna tal cual")
    void extractPath_noPrefix() {
        InvalidTransactionException ex = new InvalidTransactionException("Invalid");
        when(request.getDescription(false)).thenReturn("something-else");

        ResponseEntity<ApiResponse<?>> response = handler.handleInvalidTransactionException(ex, request);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isNotNull();
        assertThat(response.getBody().getError().getPath()).isEqualTo("something-else");
    }

    @Test
    @DisplayName("Debe manejar BankNotFoundException y retornar 404")
    void handleBankNotFoundException_returns404() {
        BankNotFoundException ex = new BankNotFoundException("Bank not found");

        ResponseEntity<ApiResponse<?>> response = handler.handleBankNotFoundException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        ApiResponse<?> body = response.getBody();
        assertBasic(body, ex.getCode(), ErrorMessagesConstants.MSG_BANK_NOT_FOUND);

        assertThat(body.getError().getDetail()).isEqualTo("Bank not found");
        assertThat(body.getError().getExceptionType()).isEqualTo("BankNotFoundException");
    }

    @Test
    @DisplayName("Debe manejar BankStrategyResolutionException y retornar 422")
    void handleBankStrategyResolutionException_returns422() {
        BankStrategyResolutionException ex = new BankStrategyResolutionException("Duplicate strategy");

        ResponseEntity<ApiResponse<?>> response = handler.handleBankStrategyResolutionException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

        ApiResponse<?> body = response.getBody();
        assertBasic(body, ex.getCode(), ErrorMessagesConstants.MSG_STRATEGY_RESOLUTION);

        assertThat(body.getError().getDetail()).isEqualTo("Duplicate strategy");
        assertThat(body.getError().getExceptionType()).isEqualTo("BankStrategyResolutionException");
    }

    static class TestDomainException extends DomainException {
        public TestDomainException(String message, String code) {
            super(message, code);
        }
    }

    @Test
    @DisplayName("Debe manejar DomainException y retornar 422")
    void handleDomainException_returns422() {
        DomainException ex = new TestDomainException("Domain broken", "ERR_DOMAIN");

        ResponseEntity<ApiResponse<?>> response = handler.handleDomainException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

        ApiResponse<?> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getCode()).isEqualTo("ERR_DOMAIN");
        assertThat(body.getMessage()).isEqualTo("Domain broken");
        assertThat(body.getError()).isNotNull();
        assertThat(body.getError().getDetail()).isEqualTo("Domain broken");
        assertThat(body.getError().getExceptionType()).isEqualTo("TestDomainException");
    }

    static class TestApplicationException extends ApplicationException {
        public TestApplicationException(String message, String code) {
            super(message, code);
        }
    }

    @Test
    @DisplayName("Debe manejar ApplicationException y retornar 500")
    void handleApplicationException_returns500() {
        ApplicationException ex = new TestApplicationException("App fail", "ERR_APP");

        ResponseEntity<ApiResponse<?>> response = handler.handleApplicationException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        ApiResponse<?> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getCode()).isEqualTo("ERR_APP");
        assertThat(body.getMessage()).isEqualTo("App fail");
        assertThat(body.getError()).isNotNull();
        assertThat(body.getError().getDetail()).isEqualTo("App fail");
        assertThat(body.getError().getExceptionType()).isEqualTo("TestApplicationException");
    }

    @Test
    @DisplayName("Debe manejar BankCommunicationException y retornar 503")
    void handleBankCommunicationException_returns503() {
        BankCommunicationException ex = new BankCommunicationException("Bank unavailable");

        ResponseEntity<ApiResponse<?>> response = handler.handleBankCommunicationException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);

        ApiResponse<?> body = response.getBody();
        assertBasic(body, ex.getCode(), ErrorMessagesConstants.MSG_BANK_UNAVAILABLE);

        assertThat(body.getError().getDetail()).isEqualTo("Bank unavailable");
        assertThat(body.getError().getExceptionType()).isEqualTo("BankCommunicationException");
    }

    @Test
    @DisplayName("ExternalServiceException: httpStatus >= 500 retorna 503")
    void handleExternalServiceException_serverError_returns503() {
        ExternalServiceException ex = mock(ExternalServiceException.class);
        when(ex.getCode()).thenReturn("ERR_EXT");
        when(ex.getMessage()).thenReturn("External down");
        when(ex.getHttpStatus()).thenReturn(BankResponseConstants.HTTP_STATUS_SERVER_ERROR_THRESHOLD);

        ResponseEntity<ApiResponse<?>> response = handler.handleExternalServiceException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertBasic(response.getBody(), "ERR_EXT", ErrorMessagesConstants.MSG_EXTERNAL_SERVICE);
    }

    @Test
    @DisplayName("ExternalServiceException: httpStatus < 500 retorna 502")
    void handleExternalServiceException_nonServerError_returns502() {
        ExternalServiceException ex = mock(ExternalServiceException.class);
        when(ex.getCode()).thenReturn("ERR_EXT");
        when(ex.getMessage()).thenReturn("External bad gateway");
        when(ex.getHttpStatus()).thenReturn(400);

        ResponseEntity<ApiResponse<?>> response = handler.handleExternalServiceException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertBasic(response.getBody(), "ERR_EXT", ErrorMessagesConstants.MSG_EXTERNAL_SERVICE);
    }

    @Test
    @DisplayName("ExternalServiceException: httpStatus null retorna 502")
    void handleExternalServiceException_nullStatus_returns502() {
        ExternalServiceException ex = mock(ExternalServiceException.class);
        when(ex.getCode()).thenReturn("ERR_EXT");
        when(ex.getMessage()).thenReturn("External unknown");
        when(ex.getHttpStatus()).thenReturn(null);

        ResponseEntity<ApiResponse<?>> response = handler.handleExternalServiceException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertBasic(response.getBody(), "ERR_EXT", ErrorMessagesConstants.MSG_EXTERNAL_SERVICE);
    }

    @Test
    @DisplayName("Debe manejar PersistenceException y retornar 500")
    void handlePersistenceException_returns500() {
        PersistenceException ex = new PersistenceException("Database error");

        ResponseEntity<ApiResponse<?>> response = handler.handlePersistenceException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertBasic(response.getBody(), ex.getCode(), ErrorMessagesConstants.MSG_DB_ERROR);
    }

    static class NamedInfrastructureException extends InfrastructureException {
        public NamedInfrastructureException(String message, String code) {
            super(message, code);
        }

        public NamedInfrastructureException(String message, String code, Throwable cause) {
            super(message, code, cause);
        }
    }

    @Test
    @DisplayName("Debe manejar InfrastructureException y retornar 500")
    void handleInfrastructureException_returns500() {
        InfrastructureException ex = new NamedInfrastructureException("Infra fail", "ERR_INFRA");

        ResponseEntity<ApiResponse<?>> response = handler.handleInfrastructureException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        ApiResponse<?> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getCode()).isEqualTo("ERR_INFRA");
        assertThat(body.getMessage()).isEqualTo("Infra fail");
        assertThat(body.getError()).isNotNull();
        assertThat(body.getError().getDetail()).isEqualTo("Infra fail");
        assertThat(body.getError().getExceptionType()).isEqualTo("NamedInfrastructureException");
    }

    @Test
    @DisplayName("InfrastructureException con cause (cubre constructor con Throwable)")
    void handleInfrastructureException_withCause() {
        RuntimeException cause = new RuntimeException("root");
        InfrastructureException ex = new NamedInfrastructureException("Infra fail", "ERR_INFRA", cause);

        ResponseEntity<ApiResponse<?>> response = handler.handleInfrastructureException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("ERR_INFRA");
        assertThat(response.getBody().getMessage()).isEqualTo("Infra fail");
    }

    @Test
    @DisplayName("Debe manejar MethodArgumentNotValidException y retornar 400 con errores")
    void handleValidationException_withFieldErrors() {
        Object target = new Object();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "dto");
        bindingResult.addError(new FieldError("dto", "amount", "must be greater than 0"));
        bindingResult.addError(new FieldError("dto", "bank", "must not be blank"));

        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(dummyMethodParameter(), bindingResult);

        ResponseEntity<ApiResponse<?>> response = handler.handleValidationException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        ApiResponse<?> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getCode()).isEqualTo(ErrorCodesConstants.ERR_VALIDATION_FAILED);
        assertThat(body.getMessage()).isEqualTo(ErrorMessagesConstants.MSG_VALIDATION_FAILED);
        assertThat(body.getError()).isNotNull();
        assertThat(body.getError().getDetail()).isEqualTo(DefaultValuesConstants.DEFAULT_VALIDATION_ERROR_MESSAGE);
        assertThat(body.getError().getExceptionType()).isEqualTo("MethodArgumentNotValidException");

        assertThat(body.getError().getValidationErrors()).isInstanceOf(Map.class);
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) body.getError().getValidationErrors();
        assertThat(errors).containsEntry("amount", "must be greater than 0");
        assertThat(errors).containsEntry("bank", "must not be blank");
    }

    @Test
    @DisplayName("ValidationException sin fieldErrors debe retornar mapa vacío")
    void handleValidationException_withoutFieldErrors() {
        Object target = new Object();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "dto");

        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(dummyMethodParameter(), bindingResult);

        ResponseEntity<ApiResponse<?>> response = handler.handleValidationException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isNotNull();

        assertThat(response.getBody().getError().getValidationErrors()).isInstanceOf(Map.class);
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) response.getBody().getError().getValidationErrors();
        assertThat(errors).isEmpty();
    }

    @Test
    @DisplayName("Debe manejar TimeoutException y retornar 504")
    void handleTimeoutException_returns504() {
        TimeoutException ex = new TimeoutException("Request timeout");

        ResponseEntity<ApiResponse<?>> response = handler.handleTimeoutException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.GATEWAY_TIMEOUT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(ErrorCodesConstants.ERR_TIMEOUT);
        assertThat(response.getBody().getMessage()).isEqualTo(ErrorMessagesConstants.MSG_TIMEOUT);
        assertThat(response.getBody().getError()).isNotNull();
        assertThat(response.getBody().getError().getDetail()).isEqualTo("Request timeout");
    }

    @Test
    @DisplayName("Debe manejar Exception genérica y retornar 500")
    void handleGenericException_returns500() {
        Exception ex = new Exception("Unexpected error");

        ResponseEntity<ApiResponse<?>> response = handler.handleGenericException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(ErrorCodesConstants.ERR_INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getMessage()).isEqualTo(ErrorMessagesConstants.MSG_INTERNAL_ERROR);
        assertThat(response.getBody().getError()).isNotNull();
        assertThat(response.getBody().getError().getDetail()).isEqualTo("Unexpected error");
    }
}