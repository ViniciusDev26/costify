package br.unifor.costify.integration.controllers;

import br.unifor.costify.TestcontainersConfiguration;
import br.unifor.costify.application.contracts.IngredientRepository;
import br.unifor.costify.domain.contracts.IdGenerator;
import br.unifor.costify.domain.entity.Ingredient;
import br.unifor.costify.domain.valueobject.Id;
import br.unifor.costify.domain.valueobject.Money;
import br.unifor.costify.domain.valueobject.Unit;
import br.unifor.costify.infra.controllers.dto.IngredientControllerRegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class IngredientUpdateControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private IngredientRepository ingredientRepository;

  @Autowired
  private IdGenerator idGenerator;

  private String existingIngredientId;

  @BeforeEach
  void setUp() {
    // Create an ingredient for testing
    Ingredient ingredient = new Ingredient(
        idGenerator,
        "Original Milk",
        1.0,
        Money.of(5.0),
        Unit.L
    );
    Ingredient saved = ingredientRepository.save(ingredient);
    existingIngredientId = saved.getId().getValue();
  }

  @AfterEach
  void tearDown() {
    // Clean up test data
    if (existingIngredientId != null) {
      ingredientRepository.deleteById(Id.of(existingIngredientId));
    }
  }

  @Test
  void shouldUpdateIngredientSuccessfully() throws Exception {
    // Arrange
    IngredientControllerRegisterRequest updateRequest = new IngredientControllerRegisterRequest(
        "Updated Skim Milk",
        2.0,
        8.50,
        Unit.L
    );

    // Act & Assert
    mockMvc.perform(put("/ingredients/{id}", existingIngredientId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", is(existingIngredientId)))
        .andExpect(jsonPath("$.name", is("Updated Skim Milk")))
        .andExpect(jsonPath("$.packageQuantity", is(2.0)))
        .andExpect(jsonPath("$.packagePrice", is(8.50)))
        .andExpect(jsonPath("$.packageUnit", is("L")));
  }

  @Test
  void shouldReturn404WhenIngredientNotFound() throws Exception {
    // Arrange
    String nonExistentId = "non-existent-id-12345";
    IngredientControllerRegisterRequest updateRequest = new IngredientControllerRegisterRequest(
        "Updated Milk",
        2.0,
        8.50,
        Unit.L
    );

    // Act & Assert
    mockMvc.perform(put("/ingredients/{id}", nonExistentId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldReturn400WhenInvalidData() throws Exception {
    // Arrange - Invalid quantity (0)
    IngredientControllerRegisterRequest invalidRequest = new IngredientControllerRegisterRequest(
        "Updated Milk",
        0.0,  // Invalid: must be > 0
        8.50,
        Unit.L
    );

    // Act & Assert
    mockMvc.perform(put("/ingredients/{id}", existingIngredientId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturn400WhenNegativePrice() throws Exception {
    // Arrange - Negative price
    IngredientControllerRegisterRequest invalidRequest = new IngredientControllerRegisterRequest(
        "Updated Milk",
        2.0,
        -5.0,  // Invalid: negative price
        Unit.L
    );

    // Act & Assert
    mockMvc.perform(put("/ingredients/{id}", existingIngredientId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturn400WhenEmptyName() throws Exception {
    // Arrange - Empty name
    IngredientControllerRegisterRequest invalidRequest = new IngredientControllerRegisterRequest(
        "",  // Invalid: empty name
        2.0,
        8.50,
        Unit.L
    );

    // Act & Assert
    mockMvc.perform(put("/ingredients/{id}", existingIngredientId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldPersistUpdatedIngredient() throws Exception {
    // Arrange
    IngredientControllerRegisterRequest updateRequest = new IngredientControllerRegisterRequest(
        "Persisted Updated Milk",
        3.0,
        12.0,
        Unit.L
    );

    // Act
    mockMvc.perform(put("/ingredients/{id}", existingIngredientId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk());

    // Assert - Verify persistence
    Ingredient updated = ingredientRepository.findById(Id.of(existingIngredientId))
        .orElseThrow(() -> new AssertionError("Ingredient should exist"));

    assert updated.getName().equals("Persisted Updated Milk");
    assert updated.getPackageQuantity() == 3.0;
    assert updated.getPackagePrice().doubleValue() == 12.0;
    assert updated.getPackageUnit() == Unit.L;
  }
}
