package br.unifor.costify.infra.errors;

/**
 * Infrastructure layer error codes for REST API responses.
 * Contains only technical/HTTP-related errors, maintaining Clean Architecture boundaries.
 * Domain and Application layer errors are mapped to these generic codes in GlobalExceptionHandler.
 */
public enum ErrorCode {
    // Generic server errors
    INTERNAL_SERVER_ERROR("INFRA-000", "Internal server error"),
    
    // Request/Validation errors  
    VALIDATION_ERROR("INFRA-001", "Request validation failed"),
    BAD_REQUEST("INFRA-400", "Bad request"),
    
    // Resource errors
    RESOURCE_NOT_FOUND("INFRA-404", "Resource not found"),
    RESOURCE_CONFLICT("INFRA-409", "Resource conflict"),
    
    // Method errors
    METHOD_NOT_ALLOWED("INFRA-405", "Method not allowed"),
    
    // Business logic errors (mapped from domain/application layers)
    BUSINESS_RULE_VIOLATION("INFRA-422", "Business rule violation"),
    BUSINESS_LOGIC_ERROR("INFRA-400", "Business logic error");

    private final String code;
    private final String defaultMessage;

    ErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public String getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}