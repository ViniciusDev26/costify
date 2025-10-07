package br.unifor.costify.application.usecase;

import br.unifor.costify.application.dto.response.UnitDto;
import br.unifor.costify.domain.valueobject.Unit;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ListAvailableUnitsUseCase {

  public List<UnitDto> execute() {
    return Arrays.stream(Unit.values()).map(UnitDto::fromDomain).toList();
  }
}
