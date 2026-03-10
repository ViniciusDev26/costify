package br.unifor.costify.catalog.infra.controllers;

import br.unifor.costify.catalog.application.dto.response.UnitDto;
import br.unifor.costify.catalog.application.usecase.ListAvailableUnitsUseCase;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/units")
public class UnitController {
  private final ListAvailableUnitsUseCase listAvailableUnitsUseCase;

  public UnitController(ListAvailableUnitsUseCase listAvailableUnitsUseCase) {
    this.listAvailableUnitsUseCase = listAvailableUnitsUseCase;
  }

  @GetMapping
  public List<UnitDto> listUnits() {
    return listAvailableUnitsUseCase.execute();
  }
}
