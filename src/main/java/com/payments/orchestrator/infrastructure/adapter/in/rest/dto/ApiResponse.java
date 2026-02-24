package com.payments.orchestrator.infrastructure.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.payments.orchestrator.infrastructure.config.constants.ResponseStatusConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private String status;

    private String code;

    private String message;

    private T data;

    private ErrorDetails error;

    private LocalDateTime timestamp;

    private String correlationId;


    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .status(ResponseStatusConstants.STATUS_SUCCESS)
                .code(ResponseStatusConstants.STATUS_SUCCESS)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Operation completed successfully");
    }

    public static <T> ApiResponse<T> error(String code, String message, ErrorDetails errorDetails) {
        return ApiResponse.<T>builder()
                .status(ResponseStatusConstants.STATUS_ERROR)
                .code(code)
                .message(message)
                .error(errorDetails)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        return error(code, message, null);
    }
}
