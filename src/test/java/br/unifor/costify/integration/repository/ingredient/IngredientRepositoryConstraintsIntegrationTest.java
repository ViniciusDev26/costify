package br.unifor.costify.integration.repository.ingredient;

import br.unifor.costify.TestcontainersConfiguration;
import br.unifor.costify.domain.entity.Ingredient;
import br.unifor.costify.domain.valueobject.Id;
import br.unifor.costify.domain.valueobject.Unit;
import br.unifor.costify.infra.data.repositories.postgres.PostgresIngredientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
class IngredientRepositoryConstraintsIntegrationTest {

    @Autowired
    private PostgresIngredientRepository ingredientRepository;

    @Test
    void save_withDuplicateName_shouldThrowDataIntegrityViolationException() {
        // Given - save first ingredient
        Ingredient firstIngredient = new Ingredient(
            Id.of("first-id"),
            "Duplicate Name",
            1.0,
            5.0,
            Unit.L
        );
        ingredientRepository.save(firstIngredient);

        // When/Then - try to save second ingredient with same name
        Ingredient secondIngredient = new Ingredient(
            Id.of("second-id"),
            "Duplicate Name", // Same name
            2.0,
            10.0,
            Unit.KG
        );

        try {
            ingredientRepository.save(secondIngredient);
            assert false : "Should have thrown DataIntegrityViolationException";
        } catch (DataIntegrityViolationException expected) {
            // Expected due to unique constraint on name
        }
    }

    @Test
    void save_withDuplicateId_shouldUpdateExistingRecord() {
        // Given - save original ingredient
        Ingredient original = new Ingredient(
            Id.of("duplicate-id"),
            "Original Name",
            1.0,
            5.0,
            Unit.L
        );
        ingredientRepository.save(original);

        // When - save new ingredient with same ID (immutable update pattern)
        Ingredient updated = new Ingredient(
            Id.of("duplicate-id"), // Same ID
            "Updated Name",        // Different name
            2.0,
            10.0,
            Unit.KG
        );
        Ingredient saved = ingredientRepository.save(updated);

        // Then - should update the existing record
        assert saved.getName().equals("Updated Name");
        assert saved.getPackageQuantity() == 2.0;
        assert saved.getPackagePrice() == 10.0;
        assert saved.getPackageUnit() == Unit.KG;

        // Verify only one record exists
        var found = ingredientRepository.findById(Id.of("duplicate-id"));
        assert found.isPresent();
        assert found.get().getName().equals("Updated Name");
    }

    @Test
    void save_withZeroPrice_shouldBeAllowed() {
        // Given - ingredient with zero price
        Ingredient freeIngredient = new Ingredient(
            Id.of("free-ingredient"),
            "Free Sample",
            1.0,
            0.0, // Zero price should be allowed
            Unit.G
        );

        // When
        Ingredient saved = ingredientRepository.save(freeIngredient);

        // Then
        assert saved.getPackagePrice() == 0.0;
        assert saved.getUnitCost() == 0.0; // Unit cost should also be zero
    }

    @Test
    void save_withLongName_shouldTruncateOrFail() {
        // Given - very long name (database column is VARCHAR(255))
        String longName = "A".repeat(300); // 300 characters
        Ingredient longNameIngredient = new Ingredient(
            Id.of("long-name-id"),
            longName,
            1.0,
            5.0,
            Unit.G
        );

        try {
            // When
            Ingredient saved = ingredientRepository.save(longNameIngredient);

            // Then - if it succeeds, name should be truncated or handled
            assert saved.getName().length() <= 255;
        } catch (Exception e) {
            // Expected - database constraint violation
            assert e instanceof DataIntegrityViolationException 
                || e.getMessage().contains("value too long");
        }
    }

    @Test
    void save_withVeryLargeNumbers_shouldHandlePrecision() {
        // Given - ingredient with large precise numbers
        Ingredient preciseIngredient = new Ingredient(
            Id.of("precise-id"),
            "Precise Ingredient",
            999.999, // 3 decimal places
            99999.99, // 2 decimal places  
            Unit.G
        );

        // When
        Ingredient saved = ingredientRepository.save(preciseIngredient);

        // Then - precision should be maintained
        assert saved.getPackageQuantity() == 999.999;
        assert saved.getPackagePrice() == 99999.99;

        // Verify after database round-trip
        var found = ingredientRepository.findById(Id.of("precise-id"));
        assert found.isPresent();
        assert found.get().getPackageQuantity() == 999.999;
        assert found.get().getPackagePrice() == 99999.99;
    }

    @Test
    void save_withAllUnits_shouldPersistCorrectly() {
        // Test all available units
        Unit[] allUnits = {Unit.G, Unit.KG, Unit.ML, Unit.L, Unit.UN};
        
        for (int i = 0; i < allUnits.length; i++) {
            Unit unit = allUnits[i];
            Ingredient ingredient = new Ingredient(
                Id.of("unit-test-" + i),
                "Test " + unit.name(),
                1.0,
                5.0,
                unit
            );

            // Save and verify
            Ingredient saved = ingredientRepository.save(ingredient);
            assert saved.getPackageUnit() == unit;

            // Verify from database
            var found = ingredientRepository.findById(saved.getId());
            assert found.isPresent();
            assert found.get().getPackageUnit() == unit;
        }
    }

    @Test
    void findById_withNullId_shouldHandleGracefully() {
        try {
            // When
            ingredientRepository.findById(null);
            assert false : "Should handle null ID gracefully";
        } catch (Exception e) {
            // Then - should throw appropriate exception
            assert e instanceof IllegalArgumentException 
                || e instanceof NullPointerException;
        }
    }

    @Test
    void existsByName_withNullName_shouldHandleGracefully() {
        try {
            // When
            ingredientRepository.existsByName(null);
            assert false : "Should handle null name gracefully";
        } catch (Exception e) {
            // Then - should throw appropriate exception  
            assert e instanceof IllegalArgumentException 
                || e instanceof NullPointerException;
        }
    }

    @Test
    void deleteById_withNonExistingId_shouldNotThrowException() {
        // Given - non-existing ID
        Id nonExistingId = Id.of("definitely-does-not-exist");

        // When/Then - should not throw exception
        ingredientRepository.deleteById(nonExistingId);

        // Verify it's still not there
        var found = ingredientRepository.findById(nonExistingId);
        assert found.isEmpty();
    }
}