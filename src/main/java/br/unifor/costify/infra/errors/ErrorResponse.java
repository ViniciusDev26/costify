package br.unifor.costify.infra.errors;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standard error response structure for all API errors
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
    LocalDateTime timestamp,
    int status,
    String error,
    String code,
    String message,
    String path,
    List<ValidationError> validationErrors) {

    public static ErrorResponse of(int status, String error, String code, String message, String path) {
        return new ErrorResponse(
            LocalDateTime.now(),
            status,
            error,
            code,
            message,
            path,
            null
        );
    }

    public static ErrorResponse of(int status, String error, String code, String message, String path, List<ValidationError> validationErrors) {
        return new ErrorResponse(
            LocalDateTime.now(),
            status,
            error,
            code,
            message,
            path,
            validationErrors
        );
    }
}