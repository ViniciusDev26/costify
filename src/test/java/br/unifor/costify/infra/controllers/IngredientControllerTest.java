package br.unifor.costify.infra.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.unifor.costify.application.dto.entity.IngredientDto;
import br.unifor.costify.application.usecase.ListIngredientsUseCase;
import br.unifor.costify.domain.valueobject.Unit;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IngredientControllerTest {

  @Mock private ListIngredientsUseCase listIngredientsUseCase;

  @InjectMocks private IngredientController ingredientController;

  private List<IngredientDto> mockIngredients;

  @BeforeEach
  void setUp() {
    IngredientDto milk =
        new IngredientDto("milk-id", "Milk", 1.0, 5.50, Unit.L, 5.50);
    IngredientDto flour =
        new IngredientDto("flour-id", "Flour", 500.0, 3.20, Unit.G, 0.0064);

    mockIngredients = List.of(milk, flour);
  }

  @Test
  void shouldReturnAllIngredients() {
    // Given
    when(listIngredientsUseCase.execute()).thenReturn(mockIngredients);

    // When
    List<IngredientDto> response = ingredientController.listIngredients();

    // Then
    assertNotNull(response);
    assertEquals(2, response.size());
    verify(listIngredientsUseCase, times(1)).execute();
  }

  @Test
  void shouldReturnEmptyList_whenNoIngredients() {
    // Given
    when(listIngredientsUseCase.execute()).thenReturn(List.of());

    // When
    List<IngredientDto> response = ingredientController.listIngredients();

    // Then
    assertNotNull(response);
    assertTrue(response.isEmpty());
    verify(listIngredientsUseCase, times(1)).execute();
  }

  @Test
  void shouldReturnIngredientsWithCorrectData() {
    // Given
    when(listIngredientsUseCase.execute()).thenReturn(mockIngredients);

    // When
    List<IngredientDto> response = ingredientController.listIngredients();

    // Then
    IngredientDto milkDto = response.get(0);
    assertEquals("milk-id", milkDto.id());
    assertEquals("Milk", milkDto.name());
    assertEquals(1.0, milkDto.packageQuantity());
    assertEquals(5.50, milkDto.packagePrice());
    assertEquals(Unit.L, milkDto.packageUnit());

    IngredientDto flourDto = response.get(1);
    assertEquals("flour-id", flourDto.id());
    assertEquals("Flour", flourDto.name());
  }
}
