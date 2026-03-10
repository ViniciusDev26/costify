package br.unifor.costify.application.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.unifor.costify.application.contracts.IngredientRepository;
import br.unifor.costify.application.dto.entity.IngredientDto;
import br.unifor.costify.domain.entity.Ingredient;
import br.unifor.costify.domain.valueobject.Id;
import br.unifor.costify.domain.valueobject.Money;
import br.unifor.costify.domain.valueobject.Unit;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListIngredientsUseCaseTest {

  @Mock private IngredientRepository ingredientRepository;

  @InjectMocks private ListIngredientsUseCase listIngredientsUseCase;

  private List<Ingredient> mockIngredients;

  @BeforeEach
  void setUp() {
    Ingredient milk = new Ingredient(Id.of("milk-id"), "Milk", 1.0, Money.of(5.50), Unit.L);
    Ingredient flour = new Ingredient(Id.of("flour-id"), "Flour", 500.0, Money.of(3.20), Unit.G);
    Ingredient sugar = new Ingredient(Id.of("sugar-id"), "Sugar", 1.0, Money.of(4.50), Unit.KG);

    mockIngredients = List.of(milk, flour, sugar);
  }

  @Test
  void shouldReturnEmptyList_whenNoIngredients() {
    // Given
    when(ingredientRepository.findAll()).thenReturn(List.of());

    // When
    List<IngredientDto> result = listIngredientsUseCase.execute();

    // Then
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(ingredientRepository, times(1)).findAll();
  }

  @Test
  void shouldReturnAllIngredients() {
    // Given
    when(ingredientRepository.findAll()).thenReturn(mockIngredients);

    // When
    List<IngredientDto> result = listIngredientsUseCase.execute();

    // Then
    assertNotNull(result);
    assertEquals(3, result.size());
    verify(ingredientRepository, times(1)).findAll();
  }

  @Test
  void shouldMapIngredientsToDto() {
    // Given
    when(ingredientRepository.findAll()).thenReturn(mockIngredients);

    // When
    List<IngredientDto> result = listIngredientsUseCase.execute();

    // Then
    IngredientDto milkDto = result.stream().filter(dto -> dto.id().equals("milk-id")).findFirst().orElseThrow();
    assertEquals("milk-id", milkDto.id());
    assertEquals("Milk", milkDto.name());
    assertEquals(1.0, milkDto.packageQuantity());
    assertEquals(5.50, milkDto.packagePrice());
    assertEquals(Unit.L, milkDto.packageUnit());

    IngredientDto flourDto = result.stream().filter(dto -> dto.id().equals("flour-id")).findFirst().orElseThrow();
    assertEquals("flour-id", flourDto.id());
    assertEquals("Flour", flourDto.name());
    assertEquals(500.0, flourDto.packageQuantity());
  }
}
