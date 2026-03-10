package br.unifor.costify.domain.errors;

/**
 * Base class for all domain-related exceptions.
 * Domain exceptions represent business rule violations and invalid domain states.
 */
public abstract class DomainException extends RuntimeException {
    
    private final DomainErrorCode errorCode;
    
    protected DomainException(DomainErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    protected DomainException(DomainErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public DomainErrorCode getDomainErrorCode() {
        return errorCode;
    }
    
    public String getErrorCode() {
        return errorCode.getCode();
    }
}