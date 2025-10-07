package br.unifor.costify.infra.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.unifor.costify.application.dto.response.UnitDto;
import br.unifor.costify.application.usecase.ListAvailableUnitsUseCase;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UnitControllerTest {

  @Mock private ListAvailableUnitsUseCase listAvailableUnitsUseCase;

  @InjectMocks private UnitController unitController;

  private List<UnitDto> mockUnitDtos;

  @BeforeEach
  void setUp() {
    mockUnitDtos =
        List.of(
            new UnitDto("ML", "VOLUME", 1.0),
            new UnitDto("L", "VOLUME", 1000.0),
            new UnitDto("G", "WEIGHT", 1.0),
            new UnitDto("KG", "WEIGHT", 1000.0),
            new UnitDto("UN", "UNIT", 1.0));
  }

  @Test
  void shouldReturnAllUnits() {
    // Given
    when(listAvailableUnitsUseCase.execute()).thenReturn(mockUnitDtos);

    // When
    List<UnitDto> response = unitController.listUnits();

    // Then
    assertNotNull(response);
    assertEquals(5, response.size());
    verify(listAvailableUnitsUseCase, times(1)).execute();
  }

  @Test
  void shouldReturnCorrectUnitData() {
    // Given
    when(listAvailableUnitsUseCase.execute()).thenReturn(mockUnitDtos);

    // When
    List<UnitDto> response = unitController.listUnits();

    // Then
    UnitDto mlUnit = response.get(0);
    assertEquals("ML", mlUnit.name());
    assertEquals("VOLUME", mlUnit.type());
    assertEquals(1.0, mlUnit.factorToBase());

    UnitDto lUnit = response.get(1);
    assertEquals("L", lUnit.name());
    assertEquals("VOLUME", lUnit.type());
    assertEquals(1000.0, lUnit.factorToBase());
  }
}
