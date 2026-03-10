package br.unifor.costify.application.usecase;

import static org.junit.jupiter.api.Assertions.*;

import br.unifor.costify.application.dto.response.UnitDto;
import java.util.List;
import org.junit.jupiter.api.Test;

class ListAvailableUnitsUseCaseTest {

  @Test
  void shouldReturnAllAvailableUnits() {
    // Given
    ListAvailableUnitsUseCase useCase = new ListAvailableUnitsUseCase();

    // When
    List<UnitDto> units = useCase.execute();

    // Then
    assertNotNull(units);
    assertEquals(7, units.size());

    // Verify some specific units
    assertTrue(units.stream().anyMatch(u -> u.name().equals("ML")));
    assertTrue(units.stream().anyMatch(u -> u.name().equals("L")));
    assertTrue(units.stream().anyMatch(u -> u.name().equals("G")));
    assertTrue(units.stream().anyMatch(u -> u.name().equals("KG")));
    assertTrue(units.stream().anyMatch(u -> u.name().equals("UN")));
  }

  @Test
  void shouldReturnUnitsWithCorrectTypes() {
    // Given
    ListAvailableUnitsUseCase useCase = new ListAvailableUnitsUseCase();

    // When
    List<UnitDto> units = useCase.execute();

    // Then
    UnitDto mlUnit = units.stream().filter(u -> u.name().equals("ML")).findFirst().orElseThrow();
    assertEquals("VOLUME", mlUnit.type());

    UnitDto gUnit = units.stream().filter(u -> u.name().equals("G")).findFirst().orElseThrow();
    assertEquals("WEIGHT", gUnit.type());

    UnitDto unUnit = units.stream().filter(u -> u.name().equals("UN")).findFirst().orElseThrow();
    assertEquals("UNIT", unUnit.type());
  }

  @Test
  void shouldReturnUnitsWithCorrectFactors() {
    // Given
    ListAvailableUnitsUseCase useCase = new ListAvailableUnitsUseCase();

    // When
    List<UnitDto> units = useCase.execute();

    // Then
    UnitDto mlUnit = units.stream().filter(u -> u.name().equals("ML")).findFirst().orElseThrow();
    assertEquals(1.0, mlUnit.factorToBase());

    UnitDto lUnit = units.stream().filter(u -> u.name().equals("L")).findFirst().orElseThrow();
    assertEquals(1000.0, lUnit.factorToBase());

    UnitDto kgUnit = units.stream().filter(u -> u.name().equals("KG")).findFirst().orElseThrow();
    assertEquals(1000.0, kgUnit.factorToBase());
  }
}
