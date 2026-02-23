package com.payments.orchestrator.infrastructure.adapter.in.rest.exception;

import com.payments.orchestrator.application.exception.ApplicationException;
import com.payments.orchestrator.application.exception.BankNotFoundException;
import com.payments.orchestrator.application.exception.BankStrategyResolutionException;
import com.payments.orchestrator.domain.exception.DomainException;
import com.payments.orchestrator.domain.exception.InvalidTransactionException;
import com.payments.orchestrator.infrastructure.config.constants.ErrorCodesConstants;
import com.payments.orchestrator.infrastructure.config.constants.ErrorMessagesConstants;
import com.payments.orchestrator.infrastructure.exception.BankCommunicationException;
import com.payments.orchestrator.infrastructure.exception.ExternalServiceException;
import com.payments.orchestrator.infrastructure.exception.InfrastructureException;
import com.payments.orchestrator.infrastructure.exception.PersistenceException;
import com.payments.orchestrator.infrastructure.adapter.in.rest.dto.ApiResponse;
import com.payments.orchestrator.infrastructure.adapter.in.rest.dto.ErrorDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidTransactionException.class)
    public ResponseEntity<ApiResponse<?>> handleInvalidTransactionException(
            InvalidTransactionException ex, WebRequest request) {

        log.warn("Invalid transaction error: {}", ex.getMessage());

        ErrorDetails errorDetails = ErrorDetails.builder()
                .path(extractPath(request))
                .timestamp(System.currentTimeMillis())
                .detail(ex.getMessage())
                .exceptionType(ex.getClass().getSimpleName())
                .build();

        ApiResponse<?> response = ApiResponse.error(
                ex.getCode(),
                ErrorMessagesConstants.MSG_INVALID_TRANSACTION,
                errorDetails
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiResponse<?>> handleDomainException(
            DomainException ex, WebRequest request) {

        log.warn("Domain error [code={}]: {}", ex.getCode(), ex.getMessage());

        ErrorDetails errorDetails = ErrorDetails.builder()
                .path(extractPath(request))
                .timestamp(System.currentTimeMillis())
                .detail(ex.getMessage())
                .exceptionType(ex.getClass().getSimpleName())
                .build();

        ApiResponse<?> response = ApiResponse.error(ex.getCode(), ex.getMessage(), errorDetails);
        return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(BankNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleBankNotFoundException(
            BankNotFoundException ex, WebRequest request) {

        log.warn("Bank not found: {}", ex.getMessage());

        ErrorDetails errorDetails = ErrorDetails.builder()
                .path(extractPath(request))
                .timestamp(System.currentTimeMillis())
                .detail(ex.getMessage())
                .exceptionType(ex.getClass().getSimpleName())
                .build();

        ApiResponse<?> response = ApiResponse.error(
                ex.getCode(),
                ErrorMessagesConstants.MSG_BANK_NOT_FOUND,
                errorDetails
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BankStrategyResolutionException.class)
    public ResponseEntity<ApiResponse<?>> handleBankStrategyResolutionException(
            BankStrategyResolutionException ex, WebRequest request) {

        log.warn("Bank strategy resolution error: {}", ex.getMessage());

        ErrorDetails errorDetails = ErrorDetails.builder()
                .path(extractPath(request))
                .timestamp(System.currentTimeMillis())
                .detail(ex.getMessage())
                .exceptionType(ex.getClass().getSimpleName())
                .build();

        ApiResponse<?> response = ApiResponse.error(
                ex.getCode(),
                ErrorMessagesConstants.MSG_STRATEGY_RESOLUTION,
                errorDetails
        );

        return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApiResponse<?>> handleApplicationException(
            ApplicationException ex, WebRequest request) {

        log.error("Application error [code={}]: {}", ex.getCode(), ex.getMessage(), ex);

        ErrorDetails errorDetails = ErrorDetails.builder()
                .path(extractPath(request))
                .timestamp(System.currentTimeMillis())
                .detail(ex.getMessage())
                .exceptionType(ex.getClass().getSimpleName())
                .build();

        ApiResponse<?> response = ApiResponse.error(ex.getCode(), ex.getMessage(), errorDetails);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BankCommunicationException.class)
    public ResponseEntity<ApiResponse<?>> handleBankCommunicationException(
            BankCommunicationException ex, WebRequest request) {

        log.error("Bank communication error: {}", ex.getMessage(), ex);

        ErrorDetails errorDetails = ErrorDetails.builder()
                .path(extractPath(request))
                .timestamp(System.currentTimeMillis())
                .detail(ex.getMessage())
                .exceptionType(ex.getClass().getSimpleName())
                .build();

        ApiResponse<?> response = ApiResponse.error(
                ex.getCode(),
                ErrorMessagesConstants.MSG_BANK_UNAVAILABLE,
                errorDetails
        );

        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ApiResponse<?>> handleExternalServiceException(
            ExternalServiceException ex, WebRequest request) {

        log.error("External service error [status={}]: {}", ex.getHttpStatus(), ex.getMessage(), ex);

        ErrorDetails errorDetails = ErrorDetails.builder()
                .path(extractPath(request))
                .timestamp(System.currentTimeMillis())
                .detail(ex.getMessage())
                .exceptionType(ex.getClass().getSimpleName())
                .build();

        ApiResponse<?> response = ApiResponse.error(
                ex.getCode(),
                ErrorMessagesConstants.MSG_EXTERNAL_SERVICE,
                errorDetails
        );

        HttpStatus status = ex.getHttpStatus() != null && ex.getHttpStatus() >= 500
                ? HttpStatus.SERVICE_UNAVAILABLE
                : HttpStatus.BAD_GATEWAY;

        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(PersistenceException.class)
    public ResponseEntity<ApiResponse<?>> handlePersistenceException(
            PersistenceException ex, WebRequest request) {

        log.error("Persistence error: {}", ex.getMessage(), ex);

        ErrorDetails errorDetails = ErrorDetails.builder()
                .path(extractPath(request))
                .timestamp(System.currentTimeMillis())
                .detail(ex.getMessage())
                .exceptionType(ex.getClass().getSimpleName())
                .build();

        ApiResponse<?> response = ApiResponse.error(
                ex.getCode(),
                ErrorMessagesConstants.MSG_DB_ERROR,
                errorDetails
        );

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InfrastructureException.class)
    public ResponseEntity<ApiResponse<?>> handleInfrastructureException(
            InfrastructureException ex, WebRequest request) {

        log.error("Infrastructure error [code={}]: {}", ex.getCode(), ex.getMessage(), ex);

        ErrorDetails errorDetails = ErrorDetails.builder()
                .path(extractPath(request))
                .timestamp(System.currentTimeMillis())
                .detail(ex.getMessage())
                .exceptionType(ex.getClass().getSimpleName())
                .build();

        ApiResponse<?> response = ApiResponse.error(ex.getCode(), ex.getMessage(), errorDetails);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {

        log.warn("Validation error: {}", ex.getMessage());

        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                validationErrors.put(error.getField(), error.getDefaultMessage())
        );

        ErrorDetails errorDetails = ErrorDetails.builder()
                .path(extractPath(request))
                .timestamp(System.currentTimeMillis())
                .detail("Campo(s) inválido(s) en la solicitud")
                .exceptionType("MethodArgumentNotValidException")
                .validationErrors(validationErrors)
                .build();

        ApiResponse<?> response = ApiResponse.error(
                ErrorCodesConstants.ERR_VALIDATION_FAILED,
                ErrorMessagesConstants.MSG_VALIDATION_FAILED,
                errorDetails
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(java.util.concurrent.TimeoutException.class)
    public ResponseEntity<ApiResponse<?>> handleTimeoutException(
            java.util.concurrent.TimeoutException ex, WebRequest request) {

        log.error("Timeout error: {}", ex.getMessage(), ex);

        ErrorDetails errorDetails = ErrorDetails.builder()
                .path(extractPath(request))
                .timestamp(System.currentTimeMillis())
                .detail(ex.getMessage())
                .exceptionType(ex.getClass().getSimpleName())
                .build();

        ApiResponse<?> response = ApiResponse.error(
                ErrorCodesConstants.ERR_TIMEOUT,
                ErrorMessagesConstants.MSG_TIMEOUT,
                errorDetails
        );

        return new ResponseEntity<>(response, HttpStatus.GATEWAY_TIMEOUT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGenericException(
            Exception ex, WebRequest request) {

        log.error("Unhandled exception: {}", ex.getMessage(), ex);

        ErrorDetails errorDetails = ErrorDetails.builder()
                .path(extractPath(request))
                .timestamp(System.currentTimeMillis())
                .detail(ex.getMessage())
                .exceptionType(ex.getClass().getSimpleName())
                .build();

        ApiResponse<?> response = ApiResponse.error(
                ErrorCodesConstants.ERR_INTERNAL,
                ErrorMessagesConstants.MSG_INTERNAL_ERROR,
                errorDetails
        );

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String extractPath(WebRequest request) {
        String path = request.getDescription(false);
        return path != null && path.startsWith("uri=") ? path.substring(4) : path;
    }
}
