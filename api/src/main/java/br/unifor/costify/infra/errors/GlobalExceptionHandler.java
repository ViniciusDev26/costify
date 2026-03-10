package br.unifor.costify.infra.errors;

import br.unifor.costify.application.errors.ApplicationErrorCode;
import br.unifor.costify.application.errors.ApplicationException;
import br.unifor.costify.domain.errors.DomainErrorCode;
import br.unifor.costify.domain.errors.DomainException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler for all REST API errors in the Costify application.
 * Provides consistent error response structure and appropriate HTTP status codes.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ====================================
    // Application Layer Exceptions
    // ====================================

    // ====================================
    // Domain Layer Exceptions
    // ====================================

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> handleApplicationException(
            ApplicationException ex, HttpServletRequest request) {
        log.warn("Application exception [{}]: {}", ex.getErrorCode(), ex.getMessage());
        
        HttpStatus status = getHttpStatusForApplicationError(ex.getApplicationErrorCode());
        
        ErrorResponse error = ErrorResponse.of(
            status.value(),
            status.getReasonPhrase(), 
            ex.getErrorCode(), // Use original application error code
            ex.getMessage(),
            request.getRequestURI()
        );
        
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(
            DomainException ex, HttpServletRequest request) {
        log.warn("Domain exception [{}]: {}", ex.getErrorCode(), ex.getMessage());
        
        ErrorResponse error = ErrorResponse.of(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request", 
            ex.getErrorCode(), // Use original domain error code
            ex.getMessage(),
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // ====================================
    // Validation Exceptions
    // ====================================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.warn("Validation failed: {}", ex.getMessage());

        List<ValidationError> validationErrors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(this::mapFieldError)
            .collect(Collectors.toList());

        ErrorResponse error = ErrorResponse.of(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            ErrorCode.VALIDATION_ERROR.getCode(),
            "Request validation failed",
            request.getRequestURI(),
            validationErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(
            BindException ex, HttpServletRequest request) {
        log.warn("Binding failed: {}", ex.getMessage());

        List<ValidationError> validationErrors = ex.getFieldErrors()
            .stream()
            .map(this::mapFieldError)
            .collect(Collectors.toList());

        ErrorResponse error = ErrorResponse.of(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            ErrorCode.VALIDATION_ERROR.getCode(),
            "Request binding failed",
            request.getRequestURI(),
            validationErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {
        log.warn("Constraint violation: {}", ex.getMessage());

        List<ValidationError> validationErrors = ex.getConstraintViolations()
            .stream()
            .map(violation -> ValidationError.of(
                violation.getPropertyPath().toString(),
                violation.getInvalidValue(),
                violation.getMessage()
            ))
            .collect(Collectors.toList());

        ErrorResponse error = ErrorResponse.of(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            ErrorCode.VALIDATION_ERROR.getCode(),
            "Constraint validation failed",
            request.getRequestURI(),
            validationErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // ====================================
    // HTTP-level Exceptions
    // ====================================

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.warn("HTTP message not readable: {}", ex.getMessage());

        String message = "Invalid JSON format";
        if (ex.getCause() instanceof InvalidFormatException invalidFormatEx) {
            message = String.format("Invalid value '%s' for field '%s'", 
                invalidFormatEx.getValue(), 
                invalidFormatEx.getPath().get(0).getFieldName());
        }

        ErrorResponse error = ErrorResponse.of(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            ErrorCode.VALIDATION_ERROR.getCode(),
            message,
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        log.warn("Method argument type mismatch: {}", ex.getMessage());

        String message = String.format("Invalid value '%s' for parameter '%s'", 
            ex.getValue(), ex.getName());

        ErrorResponse error = ErrorResponse.of(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            ErrorCode.VALIDATION_ERROR.getCode(),
            message,
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        log.warn("Method not supported: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.of(
            HttpStatus.METHOD_NOT_ALLOWED.value(),
            "Method Not Allowed",
            ErrorCode.METHOD_NOT_ALLOWED.getCode(),
            String.format("Method '%s' not supported for this endpoint", ex.getMethod()),
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(error);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFound(
            NoHandlerFoundException ex, HttpServletRequest request) {
        log.warn("No handler found: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.of(
            HttpStatus.NOT_FOUND.value(),
            "Not Found",
            ErrorCode.RESOURCE_NOT_FOUND.getCode(),
            String.format("Endpoint '%s %s' not found", ex.getHttpMethod(), ex.getRequestURL()),
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // ====================================
    // Generic Exception Handler
    // ====================================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {
        log.error("Unexpected error occurred", ex);

        ErrorResponse error = ErrorResponse.of(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
            "An unexpected error occurred. Please try again later.",
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    // ====================================
    // Helper Methods
    // ====================================

    private ValidationError mapFieldError(FieldError fieldError) {
        return ValidationError.of(
            fieldError.getField(),
            fieldError.getRejectedValue(),
            fieldError.getDefaultMessage()
        );
    }


    /**
     * Maps application error codes to appropriate HTTP status codes.
     */
    private HttpStatus getHttpStatusForApplicationError(ApplicationErrorCode applicationErrorCode) {
        return switch (applicationErrorCode) {
            case INGREDIENT_NOT_FOUND, RECIPE_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case INGREDIENT_ALREADY_EXISTS, RECIPE_ALREADY_EXISTS -> HttpStatus.CONFLICT;
            case COST_CALCULATION_ERROR, INGREDIENT_LOADING_ERROR -> HttpStatus.BAD_REQUEST;
            case APPLICATION_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }

}