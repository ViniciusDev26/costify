package br.unifor.costify.infra.errors;

/**
 * Layer-based error codes for the Costify application following Clean Architecture patterns.
 * Each error code identifies which layer the error originates from.
 */
public enum ErrorCode {
    // Infrastructure layer errors (API/Framework level)
    INTERNAL_SERVER_ERROR("INFRA-000", "Internal server error"),
    VALIDATION_ERROR("INFRA-001", "Request validation failed"),
    
    // Application layer errors (Use case level)
    INGREDIENT_NOT_FOUND("APP-100", "Ingredient not found"),
    INGREDIENT_ALREADY_EXISTS("APP-101", "Ingredient already exists"),
    RECIPE_NOT_FOUND("APP-200", "Recipe not found"),
    RECIPE_ALREADY_EXISTS("APP-201", "Recipe already exists"),
    COST_CALCULATION_ERROR("APP-300", "Error calculating recipe cost"),
    
    // Domain layer errors (Business rules level)
    INVALID_INGREDIENT_NAME("DOMAIN-100", "Invalid ingredient name"),
    NEGATIVE_MONEY("DOMAIN-200", "Money amount cannot be negative"),
    EMPTY_RECIPE("DOMAIN-300", "Recipe must have at least one ingredient"),
    INVALID_RECIPE_QUANTITY("DOMAIN-301", "Invalid recipe ingredient quantity"),
    INVALID_RECIPE_COST("DOMAIN-302", "Invalid recipe total cost"),
    
    // Domain business logic errors
    INCOMPATIBLE_UNITS("DOMAIN-400", "Cannot convert between incompatible unit types"),
    INSUFFICIENT_INVENTORY("DOMAIN-500", "Insufficient ingredient inventory");

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