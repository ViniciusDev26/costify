package br.unifor.costify.domain.errors;

/**
 * Domain error codes for business rule violations and domain exceptions.
 * These codes are owned by the domain layer and represent business concepts.
 */
public enum DomainErrorCode {
    // Ingredient domain errors
    INVALID_INGREDIENT_NAME("DOMAIN-001", "Invalid ingredient name"),
    
    // Money domain errors
    NEGATIVE_MONEY("DOMAIN-002", "Negative money amount"),
    
    // Recipe domain errors
    EMPTY_RECIPE("DOMAIN-003", "Empty recipe"),
    INVALID_QUANTITY("DOMAIN-004", "Invalid recipe quantity"),
    INVALID_TOTAL_COST("DOMAIN-005", "Invalid recipe total cost"),
    
    // Generic domain errors
    DOMAIN_CONSTRAINT_VIOLATION("DOMAIN-999", "Domain constraint violation");

    private final String code;
    private final String defaultMessage;

    DomainErrorCode(String code, String defaultMessage) {
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