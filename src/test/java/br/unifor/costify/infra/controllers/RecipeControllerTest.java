package br.unifor.costify.infra.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.unifor.costify.application.dto.entity.RecipeDto;
import br.unifor.costify.application.usecase.ListRecipesUseCase;
import br.unifor.costify.domain.valueobject.Id;
import br.unifor.costify.domain.valueobject.Money;
import br.unifor.costify.domain.valueobject.RecipeIngredient;
import br.unifor.costify.domain.valueobject.Unit;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RecipeControllerTest {

  @Mock private ListRecipesUseCase listRecipesUseCase;

  @InjectMocks private RecipeController recipeController;

  private List<RecipeDto> mockRecipes;

  @BeforeEach
  void setUp() {
    RecipeIngredient milk = new RecipeIngredient(Id.of("milk-id"), 500.0, Unit.ML);
    RecipeIngredient flour = new RecipeIngredient(Id.of("flour-id"), 300.0, Unit.G);

    RecipeDto cake = new RecipeDto("cake-id", "Cake", List.of(milk, flour), Money.of(15.50));
    RecipeDto bread = new RecipeDto("bread-id", "Bread", List.of(flour), Money.of(8.30));

    mockRecipes = List.of(cake, bread);
  }

  @Test
  void shouldReturnAllRecipes() {
    // Given
    when(listRecipesUseCase.execute()).thenReturn(mockRecipes);

    // When
    List<RecipeDto> response = recipeController.listRecipes();

    // Then
    assertNotNull(response);
    assertEquals(2, response.size());
    verify(listRecipesUseCase, times(1)).execute();
  }

  @Test
  void shouldReturnEmptyList_whenNoRecipes() {
    // Given
    when(listRecipesUseCase.execute()).thenReturn(List.of());

    // When
    List<RecipeDto> response = recipeController.listRecipes();

    // Then
    assertNotNull(response);
    assertTrue(response.isEmpty());
    verify(listRecipesUseCase, times(1)).execute();
  }

  @Test
  void shouldReturnRecipesWithCorrectData() {
    // Given
    when(listRecipesUseCase.execute()).thenReturn(mockRecipes);

    // When
    List<RecipeDto> response = recipeController.listRecipes();

    // Then
    RecipeDto cakeDto = response.get(0);
    assertEquals("cake-id", cakeDto.id());
    assertEquals("Cake", cakeDto.name());
    assertEquals(2, cakeDto.ingredients().size());

    RecipeDto breadDto = response.get(1);
    assertEquals("bread-id", breadDto.id());
    assertEquals("Bread", breadDto.name());
    assertEquals(1, breadDto.ingredients().size());
  }
}
