package br.unifor.costify.application.dto;

import br.unifor.costify.domain.valueobject.Unit;
import org.junit.jupiter.api.Test;

class RegisterIngredientCommandTest {

    @Test
    void createCommand_withValidValues_shouldInitializeCorrectly() {
        RegisterIngredientCommand command = new RegisterIngredientCommand("Flour", 1.0, 5.0, Unit.KG);

        assert command.name().equals("Flour");
        assert command.packageQuantity() == 1.0;
        assert command.packagePrice() == 5.0;
        assert command.packageUnit() == Unit.KG;
    }

    @Test
    void createCommand_withNullName_shouldThrowException() {
        try {
            new RegisterIngredientCommand(null, 1.0, 5.0, Unit.KG);
            assert false;
        } catch (IllegalArgumentException e) {
            assert e.getMessage().contains("name cannot be null or empty");
        }
    }

    @Test
    void createCommand_withEmptyName_shouldThrowException() {
        try {
            new RegisterIngredientCommand("", 1.0, 5.0, Unit.KG);
            assert false;
        } catch (IllegalArgumentException e) {
            assert e.getMessage().contains("name cannot be null or empty");
        }
    }

    @Test
    void createCommand_withBlankName_shouldThrowException() {
        try {
            new RegisterIngredientCommand("   ", 1.0, 5.0, Unit.KG);
            assert false;
        } catch (IllegalArgumentException e) {
            assert e.getMessage().contains("name cannot be null or empty");
        }
    }

    @Test
    void createCommand_withZeroQuantity_shouldThrowException() {
        try {
            new RegisterIngredientCommand("Flour", 0, 5.0, Unit.KG);
            assert false;
        } catch (IllegalArgumentException e) {
            assert e.getMessage().contains("quantity must be greater than zero");
        }
    }

    @Test
    void createCommand_withNegativeQuantity_shouldThrowException() {
        try {
            new RegisterIngredientCommand("Flour", -1.0, 5.0, Unit.KG);
            assert false;
        } catch (IllegalArgumentException e) {
            assert e.getMessage().contains("quantity must be greater than zero");
        }
    }

    @Test
    void createCommand_withNegativePrice_shouldThrowException() {
        try {
            new RegisterIngredientCommand("Flour", 1.0, -5.0, Unit.KG);
            assert false;
        } catch (IllegalArgumentException e) {
            assert e.getMessage().contains("price cannot be negative");
        }
    }

    @Test
    void createCommand_withNullUnit_shouldThrowException() {
        try {
            new RegisterIngredientCommand("Flour", 1.0, 5.0, null);
            assert false;
        } catch (IllegalArgumentException e) {
            assert e.getMessage().contains("unit cannot be null");
        }
    }
}