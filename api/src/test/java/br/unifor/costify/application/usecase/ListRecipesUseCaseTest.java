package br.unifor.costify.application.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.unifor.costify.application.contracts.RecipeRepository;
import br.unifor.costify.application.dto.entity.RecipeDto;
import br.unifor.costify.domain.entity.Recipe;
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
class ListRecipesUseCaseTest {

  @Mock private RecipeRepository recipeRepository;

  @InjectMocks private ListRecipesUseCase listRecipesUseCase;

  private List<Recipe> mockRecipes;

  @BeforeEach
  void setUp() {
    RecipeIngredient milk = new RecipeIngredient(Id.of("milk-id"), 500.0, Unit.ML);
    RecipeIngredient flour = new RecipeIngredient(Id.of("flour-id"), 300.0, Unit.G);

    Recipe cake = new Recipe(Id.of("cake-id"), "Cake", List.of(milk, flour), Money.of(15.50));
    Recipe bread = new Recipe(Id.of("bread-id"), "Bread", List.of(flour), Money.of(8.00));

    mockRecipes = List.of(cake, bread);
  }

  @Test
  void shouldReturnEmptyList_whenNoRecipes() {
    // Given
    when(recipeRepository.findAll()).thenReturn(List.of());

    // When
    List<RecipeDto> result = listRecipesUseCase.execute();

    // Then
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(recipeRepository, times(1)).findAll();
  }

  @Test
  void shouldReturnAllRecipes() {
    // Given
    when(recipeRepository.findAll()).thenReturn(mockRecipes);

    // When
    List<RecipeDto> result = listRecipesUseCase.execute();

    // Then
    assertNotNull(result);
    assertEquals(2, result.size());
    verify(recipeRepository, times(1)).findAll();
  }

  @Test
  void shouldMapRecipesToDto() {
    // Given
    when(recipeRepository.findAll()).thenReturn(mockRecipes);

    // When
    List<RecipeDto> result = listRecipesUseCase.execute();

    // Then
    RecipeDto cakeDto =
        result.stream().filter(dto -> dto.id().equals("cake-id")).findFirst().orElseThrow();
    assertEquals("cake-id", cakeDto.id());
    assertEquals("Cake", cakeDto.name());
    assertEquals(2, cakeDto.ingredients().size());

    RecipeDto breadDto =
        result.stream().filter(dto -> dto.id().equals("bread-id")).findFirst().orElseThrow();
    assertEquals("bread-id", breadDto.id());
    assertEquals("Bread", breadDto.name());
    assertEquals(1, breadDto.ingredients().size());
  }
}
