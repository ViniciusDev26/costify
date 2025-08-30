package br.unifor.costify.application.errors;

/**
 * Base class for all application-related exceptions.
 * Application exceptions represent use case failures, workflow errors, and application service issues.
 */
public abstract class ApplicationException extends RuntimeException {
    
    private final ApplicationErrorCode errorCode;
    
    protected ApplicationException(ApplicationErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    protected ApplicationException(ApplicationErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public ApplicationErrorCode getApplicationErrorCode() {
        return errorCode;
    }
    
    public String getErrorCode() {
        return errorCode.getCode();
    }
}