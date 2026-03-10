package br.unifor.costify.catalog.application.usecase;

import br.unifor.costify.catalog.application.dto.response.UnitDto;
import br.unifor.costify.shared.domain.valueobject.Unit;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ListAvailableUnitsUseCase {

  public List<UnitDto> execute() {
    return Arrays.stream(Unit.values()).map(UnitDto::fromDomain).toList();
  }
}
