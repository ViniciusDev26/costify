package br.unifor.costify.infra.controllers;

import br.unifor.costify.application.dto.response.UnitDto;
import br.unifor.costify.application.usecase.ListAvailableUnitsUseCase;
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
