package br.unifor.costify.application.errors;

/**
 * Application layer error codes for use cases and application-specific business logic.
 * These codes represent application workflow and orchestration errors.
 */
public enum ApplicationErrorCode {
    // Ingredient application errors
    INGREDIENT_NOT_FOUND("APP-100", "Ingredient not found"),
    INGREDIENT_ALREADY_EXISTS("APP-101", "Ingredient already exists"),
    
    // Recipe application errors
    RECIPE_NOT_FOUND("APP-200", "Recipe not found"),
    RECIPE_ALREADY_EXISTS("APP-201", "Recipe already exists"),
    
    // Cost calculation errors
    COST_CALCULATION_ERROR("APP-300", "Error calculating recipe cost"),
    INGREDIENT_LOADING_ERROR("APP-301", "Error loading ingredients for recipe"),
    
    // Generic application errors
    APPLICATION_ERROR("APP-999", "Application error");

    private final String code;
    private final String defaultMessage;

    ApplicationErrorCode(String code, String defaultMessage) {
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